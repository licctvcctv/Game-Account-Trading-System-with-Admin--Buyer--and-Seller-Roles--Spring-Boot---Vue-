<template>
  <div class="publish-page">
    <el-card class="hero" shadow="never">
      <div class="hero-inner">
        <div class="hero-text">
          <h2>发布账号</h2>
          <p>填写信息并上传封面，支持立即上架或先保存草稿。</p>
        </div>
        <img class="hero-ill" src="https://pic.yupi.icu/5563/202503151527812.png" alt="illustration" />
      </div>
    </el-card>

    <el-card class="form-card">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="账号名称" prop="commodityName">
              <el-input v-model="form.commodityName" maxlength="60" show-word-limit placeholder="请输入账号名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账号分类" prop="commodityTypeId">
              <el-select v-model="form.commodityTypeId" filterable clearable placeholder="请选择分类">
                <el-option v-for="t in commodityTypeList" :key="t.id" :label="t.typeName" :value="t.id" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="账号简介">
              <el-input
                v-model="form.commodityDescription"
                type="textarea"
                :rows="4"
                maxlength="500"
                show-word-limit
                placeholder="一句话介绍你的账号，支持 500 字"
              />
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="新旧程度">
              <el-select v-model="form.degree" clearable placeholder="请选择">
                <el-option label="全新" value="全新" />
                <el-option label="九成新" value="九成新" />
                <el-option label="八成新" value="八成新" />
                <el-option label="七成新" value="七成新" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="价格(¥)" prop="price">
              <el-input v-model.number="form.price" placeholder="请输入价格">
                <template #prefix>¥</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="库存" prop="commodityInventory">
              <el-input v-model.number="form.commodityInventory" placeholder="请输入库存数量" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="封面图片">
              <el-upload
                class="avatar-uploader"
                :show-file-list="false"
                :before-upload="beforeUpload"
                :http-request="doUpload"
              >
                <img v-if="form.commodityAvatar" :src="form.commodityAvatar" class="avatar" />
                <div v-else class="upload-placeholder">
                  <el-icon><Picture /></el-icon>
                  <span>点击上传</span>
                </div>
              </el-upload>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否上架">
              <el-switch v-model="form.isListed" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>
        </el-row>

        <div class="actions">
          <el-button @click="reset">重置</el-button>
          <el-button type="primary" :loading="submitting" @click="submit">发布</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
  
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { Picture } from "@element-plus/icons-vue";
import { addCommodityUsingPost } from "@/api/commodityController";
import { listCommodityTypeVoByPageUsingPost } from "@/api/commodityTypeController";
import { uploadFileUsingPost } from "@/api/fileController";

type TypeItem = { id: number; typeName: string };

const formRef = ref();
const submitting = ref(false);
const router = useRouter();
const commodityTypeList = ref<TypeItem[]>([]);

const form = ref({
  commodityName: "",
  commodityDescription: "",
  degree: "",
  commodityTypeId: undefined as unknown as number | undefined,
  isListed: 1,
  commodityInventory: 1,
  price: undefined as unknown as number | undefined,
  commodityAvatar: ""
});

const rules = {
  commodityName: [{ required: true, message: "请输入账号名称", trigger: "blur" }],
  commodityTypeId: [{ required: true, message: "请选择分类", trigger: "change" }],
  price: [{ required: true, message: "请输入价格", trigger: "blur" }],
  commodityInventory: [{ required: true, message: "请输入库存", trigger: "blur" }]
};

const loadTypes = async () => {
  const res: any = await listCommodityTypeVoByPageUsingPost({ current: 1, pageSize: 1000 });
  if (res.code === 200) {
    commodityTypeList.value = res.data.records || [];
  }
};

onMounted(() => {
  loadTypes();
});

const beforeUpload = (file: File) => {
  const isImage = /^image\/(jpeg|png|webp|svg\+xml)$/.test(file.type) || file.type === "image/svg+xml";
  const isLt1M = file.size / 1024 / 1024 < 1.0;
  if (!isImage) {
    ElMessage.error("只能上传图片类型");
    return false;
  }
  if (!isLt1M) {
    ElMessage.error("图片大小不能超过 1M");
    return false;
  }
  return true;
};

const doUpload = async (options: any) => {
  try {
    const file: File = options.file;
    const res: any = await uploadFileUsingPost({ biz: "user_avatar" }, {}, file);
    if (res.code === 200) {
      form.value.commodityAvatar = res.data;
      ElMessage.success("上传成功");
      options.onSuccess(res, file);
    } else {
      throw new Error(res.message || "上传失败");
    }
  } catch (e: any) {
    options.onError?.(e);
  }
};

const reset = () => {
  formRef.value?.resetFields();
  form.value.commodityAvatar = "";
};

const submit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return;
    try {
      submitting.value = true;
      const payload: any = {
        commodityName: form.value.commodityName,
        commodityDescription: form.value.commodityDescription,
        degree: form.value.degree,
        commodityTypeId: form.value.commodityTypeId,
        isListed: form.value.isListed,
        commodityInventory: form.value.commodityInventory,
        price: form.value.price,
        commodityAvatar: form.value.commodityAvatar
      };
      const res: any = await addCommodityUsingPost(payload);
      if (res.code === 200) {
        ElMessage.success("发布成功，已跳转到我的发布");
        router.push("/user/commodity/mine");
      } else {
        ElMessage.error(res.message || "发布失败");
      }
    } catch (e: any) {
      ElMessage.error("发布失败");
    } finally {
      submitting.value = false;
    }
  });
};
</script>

<style scoped lang="scss">
.publish-page {
  padding: 16px;
}
.hero {
  margin-bottom: 16px;
}
.hero-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.hero-text h2 {
  margin: 0;
}
.hero-text p {
  margin: 6px 0 0 0;
  color: #666;
}
.hero-ill {
  height: 72px;
  opacity: 0.9;
}
.form-card {
  padding-top: 8px;
}
.avatar-uploader {
  .avatar {
    width: 120px;
    height: 120px;
    display: block;
    border-radius: 8px;
    object-fit: cover;
    border: 1px solid #eee;
  }
  .upload-placeholder {
    width: 120px;
    height: 120px;
    border: 1px dashed var(--el-border-color);
    border-radius: 8px;
    color: var(--el-text-color-secondary);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 6px;
  }
}
.actions {
  text-align: right;
  margin-top: 8px;
}
</style>
