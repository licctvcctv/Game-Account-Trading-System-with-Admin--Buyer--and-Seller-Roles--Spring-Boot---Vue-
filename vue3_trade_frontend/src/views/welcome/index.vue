<template>
  <div style="display: flex; justify-content: center">
    <el-card style="width: 100%">
      <el-row style="font-size: 28px">欢迎使用游戏账号租售系统 🎉 </el-row>
      <el-row style="font-size: 20px; margin: 40px 0"
        >在这里，您可以便捷地发布、租用与购买各类游戏账号，支持分类浏览、订单管理与安全交易。
        🛠
      </el-row>
      <!--      介绍-->
      <el-row>
        <el-row
          >😀
          作为用户，您可以通过注册登录账户，浏览热门账号，查看最新公告，发布/租用账号，探索账号列表并管理个人主页。
        </el-row>
        <el-row style="margin: 20px 0">
          💻
          作为管理者，我们提供了用户管理、公告管理、账号管理、订单管理、评分与类别管理等功能。
        </el-row>
        <el-row>
          🤝
          我们提供多种推荐能力，帮助你快速找到合适的游戏账号，并可跟踪浏览、收藏与订单等数据，优化决策。
        </el-row>
        <el-row style="margin: 20px 0">
          👌 我们还提供订单详情、评论记录、收藏、私信等功能，保障交易体验。
        </el-row>
        <el-row style="margin-bottom: 20px"
          >🏁
          无论您是用户还是管理者，游戏账号租售系统都致力于提供稳定、安全、高效的交易服务。
        </el-row>
      </el-row>
      <el-row class="card_container" style="margin: 40px 0">
        <el-card>
          <el-row style="margin: 15px 0; font-size: 20px; font-weight: bold"
            >⭐全方位的账号推荐
          </el-row>
          <el-row style="font-size: 16px; font-weight: bold; line-height: 24px"
            >🐼平台提供丰富多样的账号推荐，涵盖各类热门游戏与区服，帮助您高效找到合适的账号。
          </el-row>
        </el-card>
        <el-card>
          <el-row style="margin: 15px 0; font-size: 20px; font-weight: bold"
            >⭐智能AI账号推荐
          </el-row>
          <el-row style="font-size: 16px; font-weight: bold; line-height: 24px"
            >🐶️平台提供AI智能对话功能，实时评估您的购物偏好，分析您的购物需求，帮助您推荐个性化商品。
          </el-row>
        </el-card>
        <el-card>
          <el-row style="margin: 15px 0; font-size: 20px; font-weight: bold"
            >⭐个人购物进度跟踪
          </el-row>
          <el-row style="font-size: 16px; font-weight: bold; line-height: 24px"
            >🐰️平台为您提供个性化的购物进度跟踪，自动记录您的购物计划，帮助您了解购物进度并优化购物安排。
          </el-row>
        </el-card>
        <el-card>
          <el-row style="margin: 15px 0; font-size: 20px; font-weight: bold"
            >⭐详尽的商品解析
          </el-row>
          <el-row style="font-size: 16px; font-weight: bold; line-height: 24px"
            >🐨平台提供详细的商品解析，帮助您深入理解每个商品的特点与亮点，让您不仅仅了解商品，更懂得如何挑选商品。
          </el-row>
        </el-card>
        <el-card>
          <el-row style="margin: 15px 0; font-size: 20px; font-weight: bold"
            >⭐多种交易方式支持
          </el-row>
          <el-row style="font-size: 16px; font-weight: bold; line-height: 24px"
            >🐷平台支持多种交易方式的推荐，您可以根据个人需求选择相应的交易方式，全面提升您的购物体验。
          </el-row>
        </el-card>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts"></script>

<style scoped lang="scss">
a {
  text-decoration: none;
  color: rgb(37, 130, 255);
  font-weight: bold;
}

.card_container {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .el-card {
    height: 250px;
    flex-basis: calc(20% - 1rem);
    margin: 0 0.5rem;
  }
}
</style>
