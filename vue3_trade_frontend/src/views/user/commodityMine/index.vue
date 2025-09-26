<template>
  <div class="mine-page">
    <template v-if="allowPublish">
      <div class="header">
        <div>
          <h2>我的发布</h2>
          <p class="sub">仅展示你发布的账号</p>
        </div>
        <div>
          <el-button type="primary" @click="goPublish" :icon="Plus"
            >发布账号</el-button
          >
        </div>
      </div>

      <el-card>
        <el-table :data="list" v-loading="loading" style="width: 100%">
          <el-table-column label="封面" width="90">
            <template #default="{ row }">
              <el-image
                :src="row.commodityAvatar"
                style="width: 64px; height: 64px; border-radius: 6px"
              />
            </template>
          </el-table-column>
          <el-table-column
            prop="commodityName"
            label="账号名称"
            min-width="160"
          />
          <el-table-column prop="commodityTypeName" label="分类" width="120" />
          <el-table-column label="类型" width="100">
            <template #default="{ row }">
              <el-tag
                size="small"
                :type="row.tradeType === 1 ? 'success' : 'warning'"
              >
                {{ row.tradeType === 1 ? "出售" : "出租" }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="价格" width="160">
            <template #default="{ row }">
              <span>{{ row.price }}</span>
              <span v-if="row.tradeType === 2" class="price-unit"> / 小时</span>
            </template>
          </el-table-column>
          <el-table-column label="库存" width="90">
            <template #default="{ row }">
              {{ row.tradeType === 2 ? "-" : row.commodityInventory }}
            </template>
          </el-table-column>
          <el-table-column label="上架" width="120">
            <template #default="{ row }">
              <el-switch
                v-model="row.isListed"
                :active-value="1"
                :inactive-value="0"
                @change="toggleListed(row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="320" fixed="right">
            <template #default="{ row }">
              <el-button size="small" type="primary" @click="openEdit(row)"
                >编辑</el-button
              >
              <el-button size="small" @click="preview(row)">预览</el-button>
              <el-popconfirm title="确定删除该账号？" @confirm="doDelete(row)">
                <template #reference>
                  <el-button size="small" type="danger">删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
        <div class="pager">
          <el-pagination
            background
            layout="total, sizes, prev, pager, next, jumper"
            :page-sizes="[6, 12, 24, 36]"
            :total="total"
            :page-size="pageSize"
            :current-page="current"
            @size-change="onSizeChange"
            @current-change="onPageChange"
          />
        </div>

        <el-dialog v-model="editVisible" title="编辑账号" width="560px">
          <el-form
            :model="editForm"
            :rules="rules"
            ref="formRef"
            label-width="90px"
          >
            <el-form-item label="账号名称" prop="commodityName">
              <el-input v-model="editForm.commodityName" />
            </el-form-item>
            <el-form-item label="账号分类" prop="commodityTypeId">
              <el-select
                v-model="editForm.commodityTypeId"
                filterable
                placeholder="请选择分类"
              >
                <el-option
                  v-for="t in commodityTypeList"
                  :key="t.id"
                  :label="t.typeName"
                  :value="t.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="交易类型" prop="tradeType">
              <el-select v-model="editForm.tradeType" placeholder="请选择类型">
                <el-option
                  v-for="opt in editableTradeTypeOptions"
                  :key="opt.value"
                  :label="opt.label"
                  :value="opt.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="账号简介">
              <el-input
                v-model="editForm.commodityDescription"
                type="textarea"
                :rows="3"
              />
            </el-form-item>
            <el-form-item :label="editPriceLabel" prop="price">
              <el-input v-model.number="editForm.price" />
            </el-form-item>
            <el-form-item
              v-if="!editIsRental"
              label="库存"
              prop="commodityInventory"
            >
              <el-input-number v-model="editForm.commodityInventory" :min="0" />
            </el-form-item>
            <el-form-item label="封面图">
              <el-input
                v-model="editForm.commodityAvatar"
                placeholder="请输入图片URL或在发布页上传后粘贴链接"
              />
            </el-form-item>
            <el-form-item label="是否上架">
              <el-switch
                v-model="editForm.isListed"
                :active-value="1"
                :inactive-value="0"
              />
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="editVisible = false">取消</el-button>
            <el-button type="primary" :loading="saving" @click="saveEdit"
              >保存</el-button
            >
          </template>
        </el-dialog>
      </el-card>
    </template>
    <el-result
      v-else
      icon="warning"
      title="暂无权限"
      sub-title="请先在个人主页申请出售或出租权限后再进行账号发布"
    >
      <template #extra>
        <el-button type="primary" @click="goAccount">前往申请</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from "vue";
import { useRouter } from "vue-router";
import {
  listMyCommodityVoByPageUsingPost,
  editCommodityUsingPost,
  deleteCommodityUsingPost
} from "@/api/commodityController";
import { listCommodityTypeVoByPageUsingPost } from "@/api/commodityTypeController";
import { Plus } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import useUserStore from "@/store/modules/user";

const router = useRouter();
const userStore = useUserStore();
const list = ref<any[]>([]);
const total = ref(0);
const pageSize = ref(12);
const current = ref(1);
const loading = ref(false);
const saving = ref(false);
const editVisible = ref(false);
const editForm = ref<any>({});
const formRef = ref();
const commodityTypeList = ref<any[]>([]);

const editIsRental = computed(() => editForm.value?.tradeType === 2);
const editPriceLabel = computed(() =>
  editIsRental.value ? "时价(¥/小时)" : "价格(¥)"
);

const allowPublish = computed(
  () =>
    (userStore.sellPermission ?? 0) === 1 ||
    (userStore.rentPermission ?? 0) === 1
);

const tradeTypeOptions = computed(() => {
  const options: { label: string; value: number }[] = [];
  if ((userStore.sellPermission ?? 0) === 1) {
    options.push({ label: "出售", value: 1 });
  }
  if ((userStore.rentPermission ?? 0) === 1) {
    options.push({ label: "出租", value: 2 });
  }
  return options;
});

const editableTradeTypeOptions = computed(() => {
  const opts = [...tradeTypeOptions.value];
  const currentType = editForm.value?.tradeType;
  if (currentType && !opts.find((item) => item.value === currentType)) {
    opts.push({
      label: currentType === 1 ? "出售" : "出租",
      value: currentType
    });
  }
  return opts;
});

const validateEditInventory = (_rule: any, value: any, callback: any) => {
  if (editIsRental.value) {
    callback();
    return;
  }
  if (value === undefined || value === null || value <= 0) {
    callback(new Error("请输入大于 0 的库存"));
  } else {
    callback();
  }
};

const rules = {
  commodityName: [
    { required: true, message: "请输入账号名称", trigger: "blur" }
  ],
  price: [{ required: true, message: "请输入价格", trigger: "blur" }],
  commodityInventory: [{ validator: validateEditInventory, trigger: "blur" }],
  commodityTypeId: [
    { required: true, message: "请选择分类", trigger: "change" }
  ],
  tradeType: [{ required: true, message: "请选择交易类型", trigger: "change" }]
};

const load = async () => {
  loading.value = true;
  try {
    const res: any = await listMyCommodityVoByPageUsingPost({
      current: current.value,
      pageSize: pageSize.value
    });
    if (res.code === 200) {
      list.value = res.data.records || [];
      total.value = parseInt(res.data.total || 0);
    } else {
      ElMessage.error(res.message || "加载失败");
    }
  } finally {
    loading.value = false;
  }
};

const onSizeChange = (s: number) => {
  pageSize.value = s;
  load();
};
const onPageChange = (p: number) => {
  current.value = p;
  load();
};

const goPublish = () => router.push("/user/commodity/publish");

const openEdit = (row: any) => {
  editForm.value = { ...row };
  if (!editForm.value.tradeType && editableTradeTypeOptions.value.length > 0) {
    editForm.value.tradeType = editableTradeTypeOptions.value[0].value;
  }
  if (editForm.value.tradeType === 2) {
    editForm.value.commodityInventory = 0;
  }
  editVisible.value = true;
};

const preview = (row: any) => {
  router.push(`/user/commodity/detail/${row.id}`);
};

const toggleListed = async (row: any) => {
  try {
    const res: any = await editCommodityUsingPost({
      id: row.id,
      isListed: row.isListed
    });
    if (res.code === 200) {
      ElMessage.success(row.isListed === 1 ? "已上架" : "已下架");
    } else {
      throw new Error(res.message || "更新失败");
    }
  } catch (e: any) {
    ElMessage.error("更新失败");
    row.isListed = row.isListed === 1 ? 0 : 1;
  }
};

const saveEdit = async () => {
  formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return;
    try {
      saving.value = true;
      const payload = {
        ...editForm.value,
        commodityInventory: editIsRental.value
          ? 0
          : editForm.value.commodityInventory
      };
      const res: any = await editCommodityUsingPost(payload);
      if (res.code === 200) {
        ElMessage.success("保存成功");
        editVisible.value = false;
        load();
      } else {
        ElMessage.error(res.message || "保存失败");
      }
    } catch (e: any) {
      ElMessage.error("保存失败");
    } finally {
      saving.value = false;
    }
  });
};

const doDelete = async (row: any) => {
  try {
    const res: any = await deleteCommodityUsingPost({ id: row.id });
    if (res.code === 200) {
      ElMessage.success("删除成功");
      load();
    } else {
      ElMessage.error(res.message || "删除失败");
    }
  } catch (e: any) {
    ElMessage.error("删除失败");
  }
};

const goAccount = () => {
  router.push("/user/account");
};

const initData = async () => {
  await load();
  const typeRes: any = await listCommodityTypeVoByPageUsingPost({
    current: 1,
    pageSize: 1000
  });
  if (typeRes.code === 200) {
    commodityTypeList.value = typeRes.data.records || [];
  }
};

watch(
  () => editForm.value?.tradeType,
  (type) => {
    if (type === 2) {
      editForm.value.commodityInventory = 0;
    }
  }
);

onMounted(async () => {
  if (!allowPublish.value) {
    ElMessage.warning("请先申请出售或出租权限");
  } else {
    await initData();
  }
});

watch(allowPublish, async (val, oldVal) => {
  if (val && !oldVal) {
    await initData();
  }
});
</script>

<style scoped lang="scss">
.mine-page {
  padding: 16px;
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
.sub {
  margin: 2px 0 0 0;
  color: #888;
  font-size: 12px;
}
.pager {
  margin-top: 12px;
  text-align: right;
}
.mb16 {
  margin-bottom: 16px;
}
.price-unit {
  margin-left: 4px;
  color: #909399;
  font-size: 12px;
}
</style>
