<template>
  <div class="mine-page">
    <div class="header">
      <div>
        <h2>我的发布</h2>
        <p class="sub">仅展示你发布的账号</p>
      </div>
      <div>
        <el-button type="primary" @click="goPublish" :icon="Plus">发布账号</el-button>
      </div>
    </div>

    <el-card>
      <el-table :data="list" v-loading="loading" style="width: 100%">
        <el-table-column label="封面" width="90">
          <template #default="{ row }">
            <el-image :src="row.commodityAvatar" style="width:64px;height:64px;border-radius:6px"/>
          </template>
        </el-table-column>
        <el-table-column prop="commodityName" label="账号名称" min-width="160" />
        <el-table-column prop="commodityTypeName" label="分类" width="120" />
        <el-table-column prop="price" label="价格(¥)" width="120" />
        <el-table-column prop="commodityInventory" label="库存" width="90" />
        <el-table-column label="上架" width="120">
          <template #default="{ row }">
            <el-switch v-model="row.isListed" :active-value="1" :inactive-value="0" @change="toggleListed(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="openEdit(row)">编辑</el-button>
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
        <el-form :model="editForm" :rules="rules" ref="formRef" label-width="90px">
          <el-form-item label="账号名称" prop="commodityName">
            <el-input v-model="editForm.commodityName" />
          </el-form-item>
          <el-form-item label="账号分类" prop="commodityTypeId">
            <el-select v-model="editForm.commodityTypeId" filterable placeholder="请选择分类">
              <el-option v-for="t in commodityTypeList" :key="t.id" :label="t.typeName" :value="t.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="账号简介">
            <el-input v-model="editForm.commodityDescription" type="textarea" :rows="3" />
          </el-form-item>
          <el-form-item label="价格(¥)" prop="price">
            <el-input v-model.number="editForm.price" />
          </el-form-item>
          <el-form-item label="库存" prop="commodityInventory">
            <el-input-number v-model="editForm.commodityInventory" :min="0" />
          </el-form-item>
          <el-form-item label="封面图">
            <el-input v-model="editForm.commodityAvatar" placeholder="请输入图片URL或在发布页上传后粘贴链接" />
          </el-form-item>
          <el-form-item label="是否上架">
            <el-switch v-model="editForm.isListed" :active-value="1" :inactive-value="0" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="editVisible=false">取消</el-button>
          <el-button type="primary" :loading="saving" @click="saveEdit">保存</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { listMyCommodityVoByPageUsingPost, editCommodityUsingPost, deleteCommodityUsingPost } from "@/api/commodityController";
import { listCommodityTypeVoByPageUsingPost } from "@/api/commodityTypeController";
import { Plus } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";

const router = useRouter();
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

const rules = {
  commodityName: [{ required: true, message: "请输入账号名称", trigger: "blur" }],
  price: [{ required: true, message: "请输入价格", trigger: "blur" }],
  commodityInventory: [{ required: true, message: "请输入库存", trigger: "blur" }],
  commodityTypeId: [{ required: true, message: "请选择分类", trigger: "change" }]
};

const load = async () => {
  loading.value = true;
  try {
    const res: any = await listMyCommodityVoByPageUsingPost({ current: current.value, pageSize: pageSize.value });
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
  editVisible.value = true;
};

const preview = (row:any) => {
  router.push(`/user/commodity/detail/${row.id}`);
};

const toggleListed = async (row:any) => {
  try {
    const res:any = await editCommodityUsingPost({ id: row.id, isListed: row.isListed });
    if (res.code === 200) {
      ElMessage.success(row.isListed === 1 ? "已上架" : "已下架");
    } else {
      throw new Error(res.message || '更新失败');
    }
  } catch(e:any) {
    ElMessage.error("更新失败");
    // 失败时回滚显示
    row.isListed = row.isListed === 1 ? 0 : 1;
  }
};

const saveEdit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return;
    try {
      saving.value = true;
      const payload = { ...editForm.value };
      const res: any = await editCommodityUsingPost(payload);
      if (res.code === 200) {
        ElMessage.success("保存成功");
        editVisible.value = false;
        load();
      } else {
        ElMessage.error(res.message || "保存失败");
      }
    } catch (e:any) {
      ElMessage.error("保存失败");
    } finally {
      saving.value = false;
    }
  });
};

const doDelete = async (row:any) => {
  try {
    const res:any = await deleteCommodityUsingPost({ id: row.id });
    if (res.code === 200) {
      ElMessage.success("删除成功");
      load();
    } else {
      ElMessage.error(res.message || "删除失败");
    }
  } catch(e:any) {
    ElMessage.error("删除失败");
  }
};

onMounted(async () => {
  await load();
  const typeRes:any = await listCommodityTypeVoByPageUsingPost({ current:1, pageSize: 1000});
  if (typeRes.code === 200) {
    commodityTypeList.value = typeRes.data.records || [];
  }
});
</script>

<style scoped lang="scss">
.mine-page { padding: 16px; }
.header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
.sub { margin: 2px 0 0 0; color: #888; font-size: 12px; }
.pager { margin-top: 12px; text-align: right; }
</style>
