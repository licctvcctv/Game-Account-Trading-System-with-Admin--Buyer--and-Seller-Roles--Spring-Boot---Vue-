<template>
  <div class="order-list-container">
    <el-card
      v-for="order in props.commodityOrderList"
      :key="order.id"
      class="order-item"
    >
      <div class="order-header">
        <div class="order-title">
          <span class="order-id">订单号：{{ order.id }}</span>
          <el-tag :type="getPayStatusTagType(order.payStatus)" size="small">
            {{ getPayStatusText(order.payStatus) }}
          </el-tag>
          <el-tag
            v-if="order.deliveryStatus !== undefined"
            :type="order.deliveryStatus === 1 ? 'success' : 'info'"
            size="small"
            style="margin-left: 8px"
          >
            {{ order.deliveryStatus === 1 ? "已发货" : "未发货" }}
          </el-tag>
          <el-tag
            v-if="order.finishStatus !== undefined"
            :type="order.finishStatus === 1 ? 'success' : 'warning'"
            size="small"
            style="margin-left: 8px"
          >
            {{ order.finishStatus === 1 ? "已完成" : "待确认" }}
          </el-tag>
        </div>
        <div class="order-actions">
          <el-button
            v-if="props.mode === 'buyer' && order.payStatus === 0"
            type="primary"
            @click="showPayDialog(order)"
          >
            支付
          </el-button>
          <el-button
            v-if="
              props.mode === 'seller' &&
              order.payStatus === 1 &&
              order.deliveryStatus !== 1
            "
            type="primary"
            @click="showDeliverDialog(order)"
          >
            发货
          </el-button>
          <el-button
            v-if="
              props.mode === 'buyer' &&
              order.payStatus === 1 &&
              order.deliveryStatus === 1 &&
              order.finishStatus !== 1
            "
            type="success"
            @click="confirmReceipt(order)"
          >
            确认收货
          </el-button>
        </div>
      </div>

      <el-divider />

      <div class="order-body">
        <div class="order-field">
          <span class="field-label">账号名称：</span>
          <span class="field-value">{{ order.commodityName }}</span>
        </div>
        <div class="order-field">
          <span class="field-label">数量：</span>
          <span class="field-value">{{ order.buyNumber }}</span>
        </div>
        <div class="order-field">
          <span class="field-label">支付金额：</span>
          <span class="field-value">{{
            formatPrice(order.paymentAmount)
          }}</span>
        </div>
        <div class="order-field">
          <span class="field-label">联系人：</span>
          <span class="field-value">{{ order.userName }}</span>
        </div>
        <div class="order-field">
          <span class="field-label">联系电话：</span>
          <span class="field-value">{{ order.userPhone }}</span>
        </div>
        <div
          class="order-field"
          v-if="props.mode === 'buyer' && order.sellerName"
        >
          <span class="field-label">卖家：</span>
          <span class="field-value">{{ order.sellerName }}</span>
        </div>
        <div class="order-field">
          <span class="field-label">创建时间：</span>
          <span class="field-value">{{ formatTime(order.createTime) }}</span>
        </div>
        <div class="order-field" v-if="order.deliverTime">
          <span class="field-label">发货时间：</span>
          <span class="field-value">{{ formatTime(order.deliverTime) }}</span>
        </div>
        <div class="order-field" v-if="order.finishTime">
          <span class="field-label">完成时间：</span>
          <span class="field-value">{{ formatTime(order.finishTime) }}</span>
        </div>
        <div
          class="order-field"
          v-if="order.deliveryContent && order.deliveryContent.length > 0"
        >
          <span class="field-label">发货内容：</span>
          <span class="field-value delivery-content">{{
            order.deliveryContent
          }}</span>
        </div>
        <div class="order-field" v-if="order.payStatus === 0">
          <span class="field-label">剩余支付时间：</span>
          <span class="field-value">
            {{ remainingTimes[order.id] || "计算中..." }}
          </span>
        </div>
      </div>
    </el-card>

    <el-dialog v-model="payDialogVisible" title="支付订单" width="360px">
      <div class="dialog-content">
        <p>订单号：{{ currentOrder?.id }}</p>
        <p>账号名称：{{ currentOrder?.commodityName }}</p>
        <p>支付金额：{{ formatPrice(currentOrder?.paymentAmount) }}</p>
      </div>
      <template #footer>
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmPay">确定支付</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="deliverDialogVisible"
      title="填写发货信息"
      width="420px"
    >
      <el-form :model="deliverForm" label-width="90px">
        <el-form-item label="发货内容" required>
          <el-input
            v-model="deliverForm.deliveryContent"
            type="textarea"
            rows="4"
            placeholder="请输入账号、卡密或其他发货信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="deliverDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmDeliver">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from "vue";
import {
  ElCard,
  ElTag,
  ElDivider,
  ElButton,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput
} from "element-plus";
import dayjs from "dayjs";

const props = defineProps({
  commodityOrderList: {
    type: Array,
    required: true
  },
  mode: {
    type: String,
    default: "buyer"
  }
});

const emit = defineEmits(["pay", "deliver", "confirm"]);

const payDialogVisible = ref(false);
const currentOrder = ref<any>(null);

const deliverDialogVisible = ref(false);
const currentDeliverOrder = ref<any>(null);
const deliverForm = ref<{ deliveryContent: string }>({ deliveryContent: "" });

const remainingTimes = ref<Record<number, string>>({});

const showPayDialog = (order: any) => {
  currentOrder.value = order;
  payDialogVisible.value = true;
};

const confirmPay = () => {
  if (currentOrder.value) {
    emit("pay", currentOrder.value.id);
    payDialogVisible.value = false;
  }
};

const showDeliverDialog = (order: any) => {
  currentDeliverOrder.value = order;
  deliverForm.value.deliveryContent = "";
  deliverDialogVisible.value = true;
};

const confirmDeliver = () => {
  if (!currentDeliverOrder.value) {
    return;
  }
  const content = deliverForm.value.deliveryContent?.trim();
  if (!content) {
    return;
  }
  emit("deliver", {
    id: currentDeliverOrder.value.id,
    deliveryContent: content
  });
  deliverDialogVisible.value = false;
};

const confirmReceipt = (order: any) => {
  emit("confirm", order.id);
};

const formatTime = (time?: string) => {
  return time ? dayjs(time).format("YYYY-MM-DD HH:mm") : "未知时间";
};

const formatPrice = (amount?: number | string) => {
  const value = Number(amount ?? 0);
  return `¥${value.toFixed(2)}`;
};

const getPayStatusTagType = (payStatus?: number) => {
  switch (payStatus) {
    case 1:
      return "success";
    case 0:
      return "danger";
    default:
      return "info";
  }
};

const getPayStatusText = (payStatus?: number) => {
  switch (payStatus) {
    case 1:
      return "支付成功";
    case 0:
      return "未支付";
    default:
      return "未知状态";
  }
};

const getRemainingTime = (createTime: string) => {
  const now = dayjs();
  const create = dayjs(createTime);
  const expireTime = create.add(15, "minute");
  const diff = expireTime.diff(now, "second");

  if (diff <= 0) {
    return "订单已过期";
  }

  const minutes = Math.floor(diff / 60);
  const seconds = diff % 60;
  return `${minutes} 分 ${seconds} 秒`;
};

const updateRemainingTimes = () => {
  props.commodityOrderList.forEach((order: any) => {
    if (order.payStatus === 0 && order.createTime) {
      remainingTimes.value[order.id] = getRemainingTime(order.createTime);
    }
  });
};

let timer: number | null = null;

onMounted(() => {
  timer = window.setInterval(updateRemainingTimes, 1000);
  updateRemainingTimes();
});

onUnmounted(() => {
  if (timer) {
    clearInterval(timer);
  }
});

watch(
  () => props.commodityOrderList,
  () => {
    updateRemainingTimes();
  },
  { immediate: true, deep: true }
);
</script>

<style scoped lang="scss">
.order-list-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 20px;
}

.order-item {
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.3s ease;

  &:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  }
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;

  .order-title {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .order-id {
    font-size: 16px;
    font-weight: bold;
    color: #333;
  }
}

.order-body {
  padding: 12px 16px;

  .order-field {
    display: flex;
    align-items: center;
    margin-bottom: 8px;

    .field-label {
      font-size: 14px;
      color: #666;
      min-width: 90px;
    }

    .field-value {
      font-size: 14px;
      color: #333;
    }

    .delivery-content {
      word-break: break-all;
      white-space: pre-wrap;
    }
  }
}

.order-actions {
  display: flex;
  gap: 8px;
}

.dialog-content {
  p {
    margin: 10px 0;
    font-size: 14px;
    color: #333;
  }
}
</style>
