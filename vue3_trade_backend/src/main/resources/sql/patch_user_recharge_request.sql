CREATE TABLE IF NOT EXISTS `user_recharge_request` (
  `id` bigint NOT NULL COMMENT '申请编号',
  `userId` bigint NOT NULL COMMENT '申请用户',
  `amount` decimal(10,2) NOT NULL COMMENT '充值金额',
  `payChannel` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '支付渠道',
  `proofUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付凭证',
  `applyRemark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '申请备注',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '0-待审核 1-已通过 2-已拒绝',
  `reviewerId` bigint DEFAULT NULL COMMENT '审核人',
  `reviewMessage` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审核备注',
  `reviewTime` datetime DEFAULT NULL COMMENT '审核时间',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_userId` (`userId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='用户充值申请';
