<template>
  <div class="recharge-admin">
    <el-card class="filter-card">
      <el-form :inline="true" :model="queryParams" label-width="80px">
        <el-form-item label="申请ID">
          <el-input
            v-model="queryParams.id"
            placeholder="请输入申请ID"
            clearable
          />
        </el-form-item>
        <el-form-item label="用户ID">
          <el-input
            v-model="queryParams.userId"
            placeholder="请输入用户ID"
            clearable
          />
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            clearable
          >
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table
        :data="rechargeList"
        border
        :loading="loading"
        style="width: 100%"
      >
        <el-table-column prop="id" label="申请ID" width="100" />
        <el-table-column prop="userId" label="用户ID" width="120" />
        <el-table-column
          prop="userName"
          label="用户名"
          width="140"
          show-overflow-tooltip
        />
        <el-table-column label="充值金额" width="120">
          <template #default="{ row }">
            ¥{{ Number(row.amount ?? 0).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="支付渠道" width="120">
          <template #default="{ row }">
            {{ formatPayChannel(row.payChannel) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusInfo(row.status).type">
              {{ getStatusInfo(row.status).text }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="applyRemark"
          label="申请备注"
          min-width="150"
          show-overflow-tooltip
        />
        <el-table-column label="付款凭证" width="120">
          <template #default="{ row }">
            <el-link
              v-if="row.proofUrl"
              :href="row.proofUrl"
              target="_blank"
              type="primary"
            >
              查看
            </el-link>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="reviewerName"
          label="审核人"
          width="140"
          show-overflow-tooltip
        />
        <el-table-column
          prop="reviewMessage"
          label="审核说明"
          min-width="150"
          show-overflow-tooltip
        />
        <el-table-column prop="createTime" label="申请时间" width="180" />
        <el-table-column prop="reviewTime" label="审核时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 0"
              size="small"
              type="success"
              @click="openReviewDialog(row, 1)"
              >通过</el-button
            >
            <el-button
              v-if="row.status === 0"
              size="small"
              type="danger"
              @click="openReviewDialog(row, 2)"
              >拒绝</el-button
            >
            <el-tag v-else type="info">已处理</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        class="pagination"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :page-sizes="[5, 10, 20, 30]"
        :total="total"
        :current-page="queryParams.current"
        :page-size="queryParams.pageSize"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </el-card>

    <el-dialog
      v-model="reviewDialogVisible"
      :title="reviewDialogTitle"
      width="420px"
    >
      <div v-if="currentRecord" class="review-summary">
        <p>申请编号：{{ currentRecord.id }}</p>
        <p>
          申请用户：{{ currentRecord.userName }} (ID:
          {{ currentRecord.userId }})
        </p>
        <p>充值金额：¥{{ Number(currentRecord.amount ?? 0).toFixed(2) }}</p>
        <p>支付渠道：{{ formatPayChannel(currentRecord.payChannel) }}</p>
      </div>
      <el-form :model="reviewForm" label-width="90px">
        <el-form-item label="审核说明">
          <el-input
            v-model="reviewForm.reviewMessage"
            type="textarea"
            rows="3"
            placeholder="可填写审核说明"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="submittingReview"
          @click="submitReview"
          >确认</el-button
        >
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
/* global API */
import { computed, onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import {
  listUserRechargeVoByPageUsingPost,
  reviewUserRechargeUsingPost
} from "@/api/userRechargeController";

const queryParams = ref({
  id: "",
  userId: "",
  status: undefined as number | undefined,
  current: 1,
  pageSize: 10
});

const rechargeList = ref<API.UserRechargeRequestVO[]>([]);
const total = ref(0);
const loading = ref(false);

const reviewDialogVisible = ref(false);
const reviewForm = ref<{ reviewMessage: string }>({ reviewMessage: "" });
const submittingReview = ref(false);
const currentRecord = ref<API.UserRechargeRequestVO | null>(null);
const currentReviewStatus = ref<1 | 2>(1);

const statusMap: Record<
  number,
  { text: string; type: "info" | "success" | "warning" | "danger" }
> = {
  0: { text: "待审核", type: "warning" },
  1: { text: "已通过", type: "success" },
  2: { text: "已拒绝", type: "danger" }
};

const getStatusInfo = (status?: number) => {
  return statusMap[status ?? -1] ?? { text: "未知状态", type: "info" };
};

const formatPayChannel = (channel?: string) => {
  if (!channel) {
    return "-";
  }
  const upper = channel.toUpperCase();
  if (upper === "WECHAT") {
    return "微信支付";
  }
  if (upper === "ALIPAY") {
    return "支付宝";
  }
  return channel;
};

const reviewDialogTitle = computed(() =>
  currentReviewStatus.value === 1 ? "通过充值申请" : "拒绝充值申请"
);

const parsePositiveInt = (value: string) => {
  const trimmed = value.trim();
  if (!trimmed) {
    return undefined;
  }
  if (!/^\d+$/.test(trimmed)) {
    return null;
  }
  return Number(trimmed);
};

const buildQueryPayload = () => {
  const payload: API.UserRechargeQueryRequest = {
    current: queryParams.value.current,
    pageSize: queryParams.value.pageSize
  };
  const idParsed = parsePositiveInt(queryParams.value.id);
  if (idParsed === null) {
    ElMessage.warning("申请ID应为数字");
    return null;
  }
  if (idParsed !== undefined) {
    payload.id = idParsed;
  }
  const userIdParsed = parsePositiveInt(queryParams.value.userId);
  if (userIdParsed === null) {
    ElMessage.warning("用户ID应为数字");
    return null;
  }
  if (userIdParsed !== undefined) {
    payload.userId = userIdParsed;
  }
  if (queryParams.value.status !== undefined) {
    payload.status = queryParams.value.status;
  }
  return payload;
};

const fetchRechargeList = async () => {
  const payload = buildQueryPayload();
  if (!payload) {
    return;
  }
  try {
    loading.value = true;
    const res = await listUserRechargeVoByPageUsingPost(payload);
    if (res.code === 200 && res.data) {
      rechargeList.value = res.data.records ?? [];
      total.value = Number(res.data.total ?? 0);
    } else {
      rechargeList.value = [];
      total.value = 0;
      ElMessage.error(res.message || "获取列表失败");
    }
  } catch (error) {
    rechargeList.value = [];
    total.value = 0;
    ElMessage.error("获取充值申请失败");
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  queryParams.value.current = 1;
  fetchRechargeList();
};

const resetQuery = () => {
  queryParams.value.id = "";
  queryParams.value.userId = "";
  queryParams.value.status = undefined;
  queryParams.value.current = 1;
  fetchRechargeList();
};

const handlePageChange = (page: number) => {
  queryParams.value.current = page;
  fetchRechargeList();
};

const handleSizeChange = (size: number) => {
  queryParams.value.pageSize = size;
  queryParams.value.current = 1;
  fetchRechargeList();
};

const openReviewDialog = (record: API.UserRechargeRequestVO, status: 1 | 2) => {
  currentRecord.value = record;
  currentReviewStatus.value = status;
  reviewForm.value.reviewMessage = "";
  reviewDialogVisible.value = true;
};

const submitReview = async () => {
  if (!currentRecord.value) {
    return;
  }
  try {
    submittingReview.value = true;
    const payload: API.UserRechargeReviewRequest = {
      id: currentRecord.value.id,
      status: currentReviewStatus.value,
      reviewMessage: reviewForm.value.reviewMessage.trim()
        ? reviewForm.value.reviewMessage.trim()
        : undefined
    };
    const res = await reviewUserRechargeUsingPost(payload);
    if (res.code === 200 && res.data) {
      ElMessage.success(
        currentReviewStatus.value === 1 ? "已通过充值申请" : "已拒绝充值申请"
      );
      reviewDialogVisible.value = false;
      await fetchRechargeList();
    } else {
      ElMessage.error(res.message || "审核失败");
    }
  } catch (error) {
    ElMessage.error("审核失败");
  } finally {
    submittingReview.value = false;
  }
};

onMounted(() => {
  fetchRechargeList();
});
</script>

<style scoped lang="scss">
.recharge-admin {
  padding: 16px;
}

.filter-card {
  margin-bottom: 16px;
}

.pagination {
  margin-top: 16px;
  text-align: right;
}

.review-summary {
  margin-bottom: 12px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;
  line-height: 1.6;
  color: #606266;
}
</style>
