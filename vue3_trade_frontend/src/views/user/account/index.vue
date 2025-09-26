<template>
  <div class="personalHomePage">
    <el-card>
      <el-tabs v-model="activeName" class="demo-tabs">
        <el-tab-pane label="个人信息" name="first">
          <el-card style="margin-bottom: 20px">
            <template #header>
              <el-row>
                <el-col :span="22" style="font-weight: bold"
                  >个人信息设置
                </el-col>
                <el-col :span="2">
                  <el-button size="default" @click="updateUserInfo"
                    >提交修改
                  </el-button>
                </el-col>
              </el-row>
            </template>
            <el-row :gutter="20" style="margin-bottom: 20px">
              <el-col>
                用户头像：
                <img
                  v-if="user.userAvatar"
                  :src="user.userAvatar"
                  class="avatar"
                  style="height: 64px; width: 64px; border-radius: 50%"
                />
              </el-col>
            </el-row>
            <el-row>
              <el-col>
                修改用户头像：
                <el-input
                  style="margin: 10px 0"
                  placeholder="请输入一个http在线图片链接，例如:https://www.xiaobaitiao.top/logo.png"
                  v-model="newUserAvatar"
                ></el-input>
              </el-col>
            </el-row>
            <!--        id-->
            <el-row style="margin-bottom: 20px"> 用户ID: {{ user.id }}</el-row>
            <!--        昵称设置-->
            <el-row style="margin-bottom: 20px">
              <el-col>
                昵称:
                <el-text v-if="!isEditing">{{ user.userName }}</el-text>
                <el-input
                  v-else
                  v-model="newUserName"
                  style="width: 200px"
                  @blur="saveEdit"
                ></el-input>
                <el-button
                  style="margin-left: 10px"
                  size="small"
                  type="primary"
                  v-if="!isEditing"
                  icon="Edit"
                  @click="startEditing"
                ></el-button>
              </el-col>
            </el-row>

            <el-row style="margin-bottom: 20px">
              <el-col>
                手机号：
                <el-input
                  v-model="user.userPhone"
                  style="width: 240px"
                  placeholder="请输入手机号"
                />
              </el-col>
            </el-row>

            <el-row style="margin-bottom: 20px">
              <el-col>
                姓名：
                <el-input
                  v-model="user.realName"
                  style="width: 240px"
                  placeholder="请输入真实姓名"
                />
              </el-col>
            </el-row>

            <el-row style="margin-bottom: 20px">
              <el-col>
                身份证号：
                <el-input
                  v-model="user.idCardNumber"
                  style="width: 320px"
                  placeholder="请输入身份证号"
                  maxlength="18"
                />
              </el-col>
            </el-row>

            <!--        用户简介-->
            <el-row style="margin-bottom: 20px">
              用户简介：
              <el-input
                type="textarea"
                v-model="user.userProfile"
                style="margin-top: 20px"
              ></el-input>
            </el-row>
            <!--        用户角色-->
            <!--        用户简介-->
            <el-row style="margin-bottom: 20px">
              用户身份：
              <el-input
                disabled
                v-model="user.userRole"
                style="margin-top: 20px"
              ></el-input>
            </el-row>
            <el-row style="margin-bottom: 20px" align="middle">
              <el-col>
                账户余额：
                <el-tag type="success"
                  >¥{{ Number(user.balance ?? 0).toFixed(2) }}</el-tag
                >
                <el-button
                  size="small"
                  type="primary"
                  style="margin-left: 12px"
                  @click="openRechargeDialog"
                  >申请充值</el-button
                >
              </el-col>
            </el-row>
            <el-row style="margin-bottom: 16px" align="middle">
              <el-col>
                出售权限：
                <el-tag
                  :type="
                    getStatusTagType(
                      user.sellPermission ?? 0,
                      user.sellApplyStatus ?? 0
                    )
                  "
                >
                  {{
                    getPermissionStatusText(
                      user.sellPermission ?? 0,
                      user.sellApplyStatus ?? 0
                    )
                  }}
                </el-tag>
                <el-button
                  v-if="canApplySell"
                  size="small"
                  style="margin-left: 12px"
                  :loading="applyLoading.sell"
                  @click="applySellerPermission('SELL')"
                  >申请出售权限</el-button
                >
              </el-col>
            </el-row>
            <el-row style="margin-bottom: 16px" align="middle">
              <el-col>
                出租权限：
                <el-tag
                  :type="
                    getStatusTagType(
                      user.rentPermission ?? 0,
                      user.rentApplyStatus ?? 0
                    )
                  "
                >
                  {{
                    getPermissionStatusText(
                      user.rentPermission ?? 0,
                      user.rentApplyStatus ?? 0
                    )
                  }}
                </el-tag>
                <el-button
                  v-if="canApplyRent"
                  size="small"
                  style="margin-left: 12px"
                  :loading="applyLoading.rent"
                  @click="applySellerPermission('RENT')"
                  >申请出租权限</el-button
                >
              </el-col>
            </el-row>
          </el-card>
          <el-card style="margin-top: 16px">
            <template #header>
              <el-row align="middle">
                <el-col :span="16" style="font-weight: bold"
                  >充值申请记录</el-col
                >
                <el-col :span="8" style="text-align: right">
                  <el-button
                    size="small"
                    :loading="rechargeLoading"
                    @click="loadRechargeList"
                    >刷新</el-button
                  >
                </el-col>
              </el-row>
            </template>
            <div class="recharge-records">
              <el-empty
                v-if="!rechargeLoading && rechargeList.length === 0"
                description="暂无充值申请"
              />
              <el-table
                v-else
                :data="rechargeList"
                size="small"
                border
                v-loading="rechargeLoading"
              >
                <el-table-column
                  prop="createTime"
                  label="申请时间"
                  min-width="160"
                />
                <el-table-column label="充值金额" min-width="120">
                  <template #default="{ row }">
                    ¥{{ Number(row.amount ?? 0).toFixed(2) }}
                  </template>
                </el-table-column>
                <el-table-column label="支付渠道" min-width="120">
                  <template #default="{ row }">
                    {{ formatPayChannel(row.payChannel) }}
                  </template>
                </el-table-column>
                <el-table-column label="状态" min-width="120">
                  <template #default="{ row }">
                    <el-tag :type="getRechargeStatus(row.status).type">
                      {{ getRechargeStatus(row.status).text }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column
                  prop="applyRemark"
                  label="备注"
                  min-width="160"
                  show-overflow-tooltip
                />
                <el-table-column label="审核说明" min-width="160">
                  <template #default="{ row }">
                    {{ row.reviewMessage || "-" }}
                  </template>
                </el-table-column>
              </el-table>
              <el-pagination
                v-if="rechargeTotal > rechargeQueryParams.pageSize"
                v-model:current-page="rechargeQueryParams.current"
                v-model:page-size="rechargeQueryParams.pageSize"
                class="recharge-pagination"
                layout="total, prev, pager, next, jumper"
                :total="rechargeTotal"
                @current-change="handleRechargePageChange"
                @size-change="handleRechargeSizeChange"
              />
            </div>
          </el-card>
        </el-tab-pane>
        <el-tab-pane label="收藏攻略" name="second">
          <Post></Post>
        </el-tab-pane>
        <el-tab-pane label="我的评论" name="third">
          <MyComment></MyComment>
        </el-tab-pane>
        <el-tab-pane label="个人订单" name="fourth">
          <div class="order-toolbar">
            <el-input
              v-model="searchOrderId"
              placeholder="请输入订单号"
              clearable
              @keyup.enter="handleOrderSearch"
              @clear="handleOrderSearch"
            />
            <el-button type="primary" @click="handleOrderSearch"
              >查询</el-button
            >
            <el-button v-if="queryParams.id" @click="resetOrderSearch"
              >重置</el-button
            >
          </div>
          <CommodityOrderList
            v-if="commodityOrderList.length > 0"
            :commodity-order-list="commodityOrderList"
            mode="buyer"
            @pay="handlePay"
            @confirm="handleConfirmReceipt"
          ></CommodityOrderList>
          <el-empty v-else description="暂无订单"></el-empty>
          <!-- 分页器 -->
          <el-pagination
            v-model:current-page="queryParams.current"
            v-model:page-size="queryParams.pageSize"
            :total="total"
            layout="total, prev, pager, next, jumper"
            @current-change="fetchCommodityOrders"
            @size-change="fetchCommodityOrders"
          />
        </el-tab-pane>
        <el-tab-pane v-if="showSellerOrders" label="出售订单" name="seller">
          <div class="order-toolbar">
            <el-input
              v-model="sellerSearchOrderId"
              placeholder="请输入订单号"
              clearable
              @keyup.enter="handleSellerOrderSearch"
              @clear="handleSellerOrderSearch"
            />
            <el-button type="primary" @click="handleSellerOrderSearch"
              >查询</el-button
            >
            <el-button
              v-if="sellerQueryParams.id"
              @click="resetSellerOrderSearch"
              >重置</el-button
            >
          </div>
          <CommodityOrderList
            v-if="sellerOrderList.length > 0"
            :commodity-order-list="sellerOrderList"
            mode="seller"
            @deliver="handleDeliver"
          ></CommodityOrderList>
          <el-empty v-else description="暂无出售订单"></el-empty>
          <el-pagination
            v-model:current-page="sellerQueryParams.current"
            v-model:page-size="sellerQueryParams.pageSize"
            :total="sellerTotal"
            layout="total, prev, pager, next, jumper"
            @current-change="handleSellerPageChange"
            @size-change="handleSellerSizeChange"
          />
        </el-tab-pane>
        <el-tab-pane label="活跃日历" name="fifth">
          <HeatmapChart :data="chartData" :year="selectedYear" />
        </el-tab-pane>
        <!-- 收藏账号 -->
        <el-tab-pane label="收藏账号" name="sixth">
          <CommodityList :commodity-list="commodityList"></CommodityList>
          <!-- 分页器 -->
          <el-pagination
            v-model:current-page="favoritesQueryParams.current"
            v-model:page-size="favoritesQueryParams.pageSize"
            :total="favoritesTotal"
            layout="total, prev, pager, next, jumper"
            @current-change="loadCommodityFavoritesList"
            @size-change="loadCommodityFavoritesList"
          />
        </el-tab-pane>
        <el-tab-pane label="聊天室" name="seventh">
          <PrivateMessage></PrivateMessage>
        </el-tab-pane>
      </el-tabs>
    </el-card>
    <el-dialog v-model="rechargeDialogVisible" title="充值余额" width="480px">
      <el-form :model="rechargeForm" label-width="90px" class="recharge-form">
        <el-form-item label="充值金额" required>
          <el-input-number
            v-model="rechargeForm.amount"
            :min="1"
            :precision="2"
            :step="10"
            placeholder="请输入充值金额"
          />
        </el-form-item>
        <el-form-item label="支付渠道" required>
          <el-radio-group
            v-model="rechargeForm.payChannel"
            class="pay-channel-group"
          >
            <el-radio-button
              v-for="option in payChannelOptions"
              :key="option.value"
              :label="option.value"
            >
              {{ option.label }}
            </el-radio-button>
          </el-radio-group>
          <div v-if="selectedPayChannel" class="pay-preview">
            <img :src="selectedPayChannel.qr" alt="支付二维码" />
            <div class="pay-preview-text">
              <p>{{ selectedPayChannel.description }}</p>
              <a :href="selectedPayChannel.link" target="_blank" rel="noopener"
                >打开示例链接</a
              >
            </div>
          </div>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="rechargeForm.applyRemark"
            type="textarea"
            rows="2"
            placeholder="可填写备注信息，便于管理员审核"
          />
        </el-form-item>
        <el-form-item label="凭证链接">
          <el-input
            v-model="rechargeForm.proofUrl"
            placeholder="可粘贴支付截图或凭证链接"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rechargeDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="submittingRecharge"
          @click="submitRecharge"
        >
          提交申请
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
/* global API */
import { computed, onMounted, ref } from "vue";
import { GET_ID } from "@/utils/token";
import PrivateMessage from "@/components/PrivateMessage/index.vue";
import Post from "@/components/Post/index.vue";
import MyComment from "@/components/MyComment/index.vue";
import CommodityList from "@/components/CommodityList/index.vue";
import {
  getUserVoByIdUsingGet,
  updateMyUserUsingPost,
  applySellerUsingPost
} from "@/api/userController";
import {
  applyUserRechargeUsingPost,
  listMyUserRechargeVoByPageUsingPost
} from "@/api/userRechargeController";
import { ElMessage, ElPagination } from "element-plus";
import useUserStore from "@/store/modules/user";
import CommodityOrderList from "@/components/CommodityOrderList/index.vue";
import {
  getCommodityOrderHeatmapDataUsingGet,
  listMyCommodityOrderVoByPageUsingPost,
  listMySellCommodityOrderVoByPageUsingPost,
  deliverCommodityOrderUsingPost,
  confirmCommodityOrderUsingPost
} from "@/api/commodityOrderController";
import { payCommodityOrderUsingPost } from "@/api/commodityController";
import HeatmapChart from "@/components/CalendarChart/index.vue";
import { listUserCommodityFavoritesVoByPageUsingPost } from "@/api/userCommodityFavoritesController";
// 定义响应式数据
const activeName = ref("first");
const userStore = useUserStore();
const newUserAvatar = ref();
const newUserName = ref("");
const isEditing = ref(false);
// 定义数据
const chartData = ref<{ date: string; value: number }[]>([]);
const selectedYear = ref("2025"); // 假设默认年份为 2025
// 总条数
const total = ref(0);
// 订单列表数据
const commodityOrderList = ref([]);
const searchOrderId = ref("");
const sellerOrderList = ref([]);
const sellerTotal = ref(0);
const sellerSearchOrderId = ref("");
const sellerQueryParams = ref<{
  current: number;
  pageSize: number;
  id?: string;
}>({
  current: 1,
  pageSize: 10
});
const user = ref({
  id: 0,
  userAvatar: "",
  userName: "",
  userProfile: "",
  userRole: "",
  userPhone: "",
  realName: "",
  idCardNumber: "",
  balance: 0,
  sellPermission: 0,
  rentPermission: 0,
  sellApplyStatus: 0,
  rentApplyStatus: 0
});
// 账号列表数据
const commodityList = ref([]);
// 分页参数
const queryParams = ref<{ current: number; pageSize: number; id?: string }>({
  current: 1,
  pageSize: 10
});
// 调用后端接口获取数据
const fetchTravelData = async (userId: number, payStatus: number) => {
  try {
    const res = await getCommodityOrderHeatmapDataUsingGet({
      userId,
      payStatus
    });
    if (res.code === 200) {
      chartData.value = res.data;
    } else {
      ElMessage.error("获取数据失败:", res.message);
    }
  } catch (error: any) {
    ElMessage.error("请求失败:", error);
  }
};

const favoritesQueryParams = ref({
  current: 1,
  pageSize: 10,
  userId: GET_ID(),
  status: 1 // 1 表示正常收藏
});
const favoritesTotal = ref(0);
const rechargeDialogVisible = ref(false);
const rechargeForm = ref<{
  amount: number | null;
  payChannel: "WECHAT" | "ALIPAY";
  applyRemark: string;
  proofUrl: string;
}>({
  amount: null,
  payChannel: "WECHAT",
  applyRemark: "",
  proofUrl: ""
});
const submittingRecharge = ref(false);
const payChannelOptions: Array<{
  value: "WECHAT" | "ALIPAY";
  label: string;
  description: string;
  link: string;
  qr: string;
}> = [
  {
    value: "WECHAT",
    label: "微信支付",
    description: "打开微信扫一扫完成支付，随后提交充值申请由管理员审核。",
    link: "https://pay.wechat.com",
    qr: "https://api.qrserver.com/v1/create-qr-code/?size=160x160&data=mock-wechat"
  },
  {
    value: "ALIPAY",
    label: "支付宝支付",
    description: "使用支付宝扫码支付，提交后等待管理员审核。",
    link: "https://www.alipay.com",
    qr: "https://api.qrserver.com/v1/create-qr-code/?size=160x160&data=mock-alipay"
  }
];
const selectedPayChannel = computed(() =>
  payChannelOptions.find(
    (option) => option.value === rechargeForm.value.payChannel
  )
);
const rechargeQueryParams = ref({ current: 1, pageSize: 5 });
const rechargeTotal = ref(0);
const rechargeList = ref<API.UserRechargeRequestVO[]>([]);
const rechargeLoading = ref(false);
const applyLoading = ref({ sell: false, rent: false });

const permissionStatusMap: Record<number, string> = {
  0: "未申请",
  1: "审核中",
  2: "已通过",
  3: "已拒绝"
};

const getPermissionStatusText = (permission: number, status: number) => {
  if (permission === 1) {
    return "已开通";
  }
  return permissionStatusMap[status] ?? "未申请";
};

const getStatusTagType = (permission: number, status: number) => {
  if (permission === 1) {
    return "success";
  }
  if (status === 1) {
    return "warning";
  }
  if (status === 3) {
    return "danger";
  }
  return "info";
};

const rechargeStatusMap: Record<
  number,
  { text: string; type: "warning" | "success" | "danger" | "info" }
> = {
  0: { text: "待审核", type: "warning" },
  1: { text: "已通过", type: "success" },
  2: { text: "已拒绝", type: "danger" }
};

const getRechargeStatus = (status?: number) => {
  return rechargeStatusMap[status ?? -1] ?? { text: "未知状态", type: "info" };
};

const formatPayChannel = (channel?: string) => {
  if (!channel) {
    return "-";
  }
  const normalized = channel.toUpperCase();
  const option = payChannelOptions.find((item) => item.value === normalized);
  return option?.label ?? channel;
};

const canApplySell = computed(() => {
  return (
    (user.value.sellPermission ?? 0) !== 1 && user.value.sellApplyStatus !== 1
  );
});

const canApplyRent = computed(() => {
  return (
    (user.value.rentPermission ?? 0) !== 1 && user.value.rentApplyStatus !== 1
  );
});

const showSellerOrders = computed(() => {
  return (user.value.sellPermission ?? 0) === 1;
});

// 加载收藏账号列表
const loadCommodityFavoritesList = async () => {
  try {
    const res = await listUserCommodityFavoritesVoByPageUsingPost(
      favoritesQueryParams.value
    );

    if (res.code === 200 && res.data?.records) {
      commodityList.value = res.data.records.map((item) => {
        return {
          ...item, // 保留其他字段
          id: item.commodityId // 将 spotId 重命名为 id
        };
      });
      favoritesTotal.value = res.data.total;
    } else {
      ElMessage.warning("暂无收藏账号");
    }
  } catch (error) {
    ElMessage.error("获取收藏账号失败");
  }
};

const resetRechargeForm = () => {
  rechargeForm.value.amount = null;
  rechargeForm.value.applyRemark = "";
  rechargeForm.value.proofUrl = "";
  if (!rechargeForm.value.payChannel) {
    rechargeForm.value.payChannel = "WECHAT";
  }
};

const openRechargeDialog = () => {
  resetRechargeForm();
  rechargeDialogVisible.value = true;
};

const submitRecharge = async () => {
  const amount = Number(rechargeForm.value.amount ?? 0);
  if (!Number.isFinite(amount) || amount <= 0) {
    return ElMessage.error("请输入有效的充值金额");
  }
  if (!rechargeForm.value.payChannel) {
    return ElMessage.error("请选择支付渠道");
  }
  try {
    submittingRecharge.value = true;
    const payload: API.UserRechargeApplyRequest = {
      amount,
      payChannel: rechargeForm.value.payChannel,
      applyRemark: rechargeForm.value.applyRemark?.trim() || undefined,
      proofUrl: rechargeForm.value.proofUrl?.trim() || undefined
    };
    const res = await applyUserRechargeUsingPost(payload);
    if (res.code === 200) {
      ElMessage.success("充值申请已提交，请等待管理员审核");
      rechargeDialogVisible.value = false;
      await loadRechargeList();
    } else {
      ElMessage.error(res.message || "提交失败");
    }
  } catch (error: any) {
    ElMessage.error(error?.message || "提交失败");
  } finally {
    submittingRecharge.value = false;
  }
};

const loadRechargeList = async () => {
  try {
    rechargeLoading.value = true;
    const res = await listMyUserRechargeVoByPageUsingPost({
      current: rechargeQueryParams.value.current,
      pageSize: rechargeQueryParams.value.pageSize
    });
    if (res.code === 200 && res.data) {
      rechargeList.value = res.data.records ?? [];
      rechargeTotal.value = Number(res.data.total ?? 0);
    } else {
      rechargeList.value = [];
      rechargeTotal.value = 0;
    }
  } catch (error) {
    rechargeList.value = [];
    rechargeTotal.value = 0;
    ElMessage.error("获取充值记录失败");
  } finally {
    rechargeLoading.value = false;
  }
};

const handleRechargePageChange = (page: number) => {
  rechargeQueryParams.value.current = page;
  loadRechargeList();
};

const handleRechargeSizeChange = (size: number) => {
  rechargeQueryParams.value.pageSize = size;
  rechargeQueryParams.value.current = 1;
  loadRechargeList();
};

const applySellerPermission = async (type: "SELL" | "RENT") => {
  const key = type === "SELL" ? "sell" : "rent";
  if (type === "SELL" && !canApplySell.value) {
    return ElMessage.warning("当前不可申请出售权限");
  }
  if (type === "RENT" && !canApplyRent.value) {
    return ElMessage.warning("当前不可申请出租权限");
  }
  try {
    applyLoading.value[key] = true;
    const res = await applySellerUsingPost({ applyType: type });
    if (res.code === 200) {
      ElMessage.success("申请已提交，请等待审核");
      await getUserInformationById();
    } else {
      ElMessage.error(res.message || "申请失败");
    }
  } catch (error: any) {
    ElMessage.error(error?.message || "申请失败");
  } finally {
    applyLoading.value[key] = false;
  }
};
// 处理支付
const handlePay = async (orderId: number) => {
  try {
    const response = await payCommodityOrderUsingPost({
      commodityOrderId: orderId
    });
    if (response.code === 200) {
      ElMessage.success("支付成功");
      await fetchCommodityOrders();
      await getUserInformationById();
    } else {
      ElMessage.error("支付失败，" + response.message);
    }
  } catch (error) {
    ElMessage.error("支付失败");
  }
};
const handleOrderSearch = () => {
  const trimmed = searchOrderId.value.trim();
  if (trimmed && !/^\d+$/.test(trimmed)) {
    ElMessage.warning("请输入有效的订单号");
    return;
  }
  queryParams.value.current = 1;
  queryParams.value.id = trimmed ? trimmed : undefined;
  fetchCommodityOrders();
};

const resetOrderSearch = () => {
  searchOrderId.value = "";
  queryParams.value.id = undefined;
  queryParams.value.current = 1;
  fetchCommodityOrders();
};

// 获取商品订单数据
const fetchCommodityOrders = async () => {
  try {
    const payload: Record<string, any> = {
      current: queryParams.value.current,
      pageSize: queryParams.value.pageSize
    };
    if (queryParams.value.id) {
      payload.id = queryParams.value.id;
    }
    const response = await listMyCommodityOrderVoByPageUsingPost(payload);
    if (response.data?.records) {
      commodityOrderList.value = response.data.records;
      total.value = Number(response.data.total ?? 0);
    } else {
      commodityOrderList.value = [];
      total.value = 0;
      ElMessage.warning("暂无订单数据");
    }
  } catch (error) {
    ElMessage.error("获取订单数据失败");
  }
};

const handleSellerOrderSearch = () => {
  const trimmed = sellerSearchOrderId.value.trim();
  if (trimmed && !/^\d+$/.test(trimmed)) {
    ElMessage.warning("请输入有效的订单号");
    return;
  }
  sellerQueryParams.value.current = 1;
  sellerQueryParams.value.id = trimmed ? trimmed : undefined;
  fetchSellerOrders();
};

const resetSellerOrderSearch = () => {
  sellerSearchOrderId.value = "";
  sellerQueryParams.value.id = undefined;
  sellerQueryParams.value.current = 1;
  fetchSellerOrders();
};

const fetchSellerOrders = async () => {
  if (!showSellerOrders.value) {
    sellerOrderList.value = [];
    sellerTotal.value = 0;
    return;
  }
  try {
    const payload: Record<string, any> = {
      current: sellerQueryParams.value.current,
      pageSize: sellerQueryParams.value.pageSize
    };
    if (sellerQueryParams.value.id) {
      payload.id = sellerQueryParams.value.id;
    }
    const response = await listMySellCommodityOrderVoByPageUsingPost(payload);
    if (response.data?.records) {
      sellerOrderList.value = response.data.records;
      sellerTotal.value = Number(response.data.total ?? 0);
    } else {
      sellerOrderList.value = [];
      sellerTotal.value = 0;
    }
  } catch (error) {
    sellerOrderList.value = [];
    sellerTotal.value = 0;
    ElMessage.error("获取出售订单失败");
  }
};

const handleSellerPageChange = (page: number) => {
  sellerQueryParams.value.current = page;
  fetchSellerOrders();
};

const handleSellerSizeChange = (size: number) => {
  sellerQueryParams.value.pageSize = size;
  sellerQueryParams.value.current = 1;
  fetchSellerOrders();
};

const handleDeliver = async (payload: {
  id: number;
  deliveryContent: string;
}) => {
  if (!payload?.id) {
    return;
  }
  try {
    const response = await deliverCommodityOrderUsingPost({
      id: payload.id,
      deliveryContent: payload.deliveryContent
    });
    if (response.code === 200) {
      ElMessage.success("发货信息已提交");
      await fetchSellerOrders();
      await fetchCommodityOrders();
    } else {
      ElMessage.error(response.message || "发货失败");
    }
  } catch (error) {
    ElMessage.error("发货失败");
  }
};

const handleConfirmReceipt = async (orderId: number) => {
  if (!orderId) {
    return;
  }
  try {
    const response = await confirmCommodityOrderUsingPost({ id: orderId });
    if (response.code === 200) {
      ElMessage.success("已确认收货");
      await fetchCommodityOrders();
      if (showSellerOrders.value) {
        await fetchSellerOrders();
      }
      await getUserInformationById();
    } else {
      ElMessage.error(response.message || "确认收货失败");
    }
  } catch (error) {
    ElMessage.error("确认收货失败");
  }
};

onMounted(() => {
  getUserInformationById();
  fetchCommodityOrders();
  fetchTravelData(GET_ID(), 1);
  loadCommodityFavoritesList();
  loadRechargeList();
});
const updateUserInfo = async () => {
  const res = await updateMyUserUsingPost({
    userAvatar: newUserAvatar.value || user.value.userAvatar,
    userName: user.value.userName,
    userProfile: user.value.userProfile,
    userPhone: user.value.userPhone,
    realName: user.value.realName,
    idCardNumber: user.value.idCardNumber
  });
  if (res.code !== 200) {
    return ElMessage.error({
      duration: 1000,
      message: "更新用户信息失败"
    });
  }
  // 调用 userStore 的 updateAvatar 方法
  await userStore.updateAvatar(newUserAvatar.value || user.value.userAvatar);
  ElMessage.success({
    duration: 1000,
    message: "更新用户信息成功"
  });
  await getUserInformationById();
};
const getUserInformationById = async () => {
  const id = GET_ID();
  let result: any = await getUserVoByIdUsingGet({
    id: id
  });
  if (result.code == 200) {
    user.value = result.data;
    user.value.userPhone = result.data.userPhone ?? "";
    user.value.realName = result.data.realName ?? "";
    user.value.idCardNumber = result.data.idCardNumber ?? "";
    userStore.syncSellerInfo({
      sellPermission: result.data.sellPermission,
      rentPermission: result.data.rentPermission,
      sellApplyStatus: result.data.sellApplyStatus,
      rentApplyStatus: result.data.rentApplyStatus
    });
    if ((result.data.sellPermission ?? 0) === 1) {
      await fetchSellerOrders();
    } else {
      sellerOrderList.value = [];
      sellerTotal.value = 0;
    }
  }
};
const startEditing = () => {
  isEditing.value = true;
  newUserName.value = user.value.userName;
};
const saveEdit = () => {
  isEditing.value = false;
  user.value.userName = newUserName.value;
};
</script>

<style lang="scss">
.el-card__header {
  background-color: #fafafa;
}

.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 96px;
  height: 96px;
  text-align: center;
}

.order-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 20px 16px;
}

.order-toolbar .el-input {
  max-width: 280px;
}

.recharge-records {
  padding: 12px 0;
}

.recharge-pagination {
  margin-top: 16px;
  text-align: right;
}

.recharge-form .pay-channel-group {
  margin-bottom: 12px;
}

.pay-preview {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-top: 12px;
}

.pay-preview img {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  object-fit: cover;
}

.pay-preview-text p {
  margin: 0 0 6px;
  color: #606266;
  font-size: 14px;
}

.pay-preview-text a {
  font-size: 14px;
  color: var(--el-color-primary);
}
</style>
