/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50716
 Source Host           : localhost:3306
 Source Schema         : trade

 Target Server Type    : MySQL
 Target Server Version : 50716
 File Encoding         : 65001

 Date: 16/03/2025 14:25:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for barrage
-- ----------------------------
DROP TABLE IF EXISTS `barrage`;
CREATE TABLE `barrage`  (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                            `message` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '弹幕文本',
                            `userAvatar` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户头像',
                            `userId` bigint(20) NOT NULL COMMENT '用户id',
                            `isSelected` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否精选（默认0，精选为1）',
                            `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `isDelete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1890696292486328323 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评论 ID',
                            `postId` bigint(20) NOT NULL COMMENT '面经帖子 ID',
                            `userId` bigint(20) NOT NULL COMMENT '用户 ID',
                            `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '评论内容',
                            `parentId` bigint(20) NULL DEFAULT NULL COMMENT '父评论 ID，支持多级嵌套回复',
                            `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `isDelete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
                            `ancestorId` bigint(20) NULL DEFAULT NULL,
                            PRIMARY KEY (`id`) USING BTREE,
                            INDEX `comment_questionId`(`postId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1890696721316163587 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for commodity
-- ----------------------------
DROP TABLE IF EXISTS `commodity`;
CREATE TABLE `commodity`  (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品 ID',
                              `commodityName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '商品名称',
                              `commodityDescription` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品简介',
                              `commodityAvatar` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品封面图',
                              `degree` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品新旧程度（例如 9成新）',
                              `commodityTypeId` bigint(20) NULL DEFAULT NULL COMMENT '商品分类 ID',
                              `adminId` bigint(20) NOT NULL COMMENT '管理员 ID （某人创建该商品）',
                              `isListed` tinyint(4) NULL DEFAULT 0 COMMENT '是否上架（默认0未上架，1已上架）',
                              `commodityInventory` int(10) NULL DEFAULT 0 COMMENT '商品数量（默认0）',
                              `price` decimal(10, 2) NOT NULL COMMENT '商品价格',
                              `viewNum` int(11) NULL DEFAULT 0 COMMENT '商品浏览量',
                              `favourNum` int(11) NULL DEFAULT 0 COMMENT '商品收藏量',
                              `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              `isDelete` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除',
                              PRIMARY KEY (`id`) USING BTREE,
                              INDEX `type_index`(`commodityTypeId`) USING BTREE,
                              INDEX `name_index`(`commodityName`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1900439195794169858 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for commodity_order
-- ----------------------------
DROP TABLE IF EXISTS `commodity_order`;
CREATE TABLE `commodity_order`  (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单 ID',
                                    `userId` bigint(20) NOT NULL COMMENT '用户 ID',
                                    `commodityId` bigint(20) NOT NULL COMMENT '商品 ID',
                                    `remark` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单备注',
                                    `buyNumber` int(10) NULL DEFAULT NULL COMMENT '购买数量',
                                    `paymentAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '订单总支付金额',
                                    `payStatus` tinyint(4) NULL DEFAULT 0 COMMENT '0-未支付 1-已支付',
                                    `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `isDelete` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    UNIQUE INDEX `payId`(`userId`, `commodityId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1900058394921512962 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for commodity_score
-- ----------------------------
DROP TABLE IF EXISTS `commodity_score`;
CREATE TABLE `commodity_score`  (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品评分 ID',
                                    `commodityId` bigint(20) NOT NULL COMMENT '商品 ID',
                                    `userId` bigint(20) NOT NULL COMMENT '用户 ID',
                                    `score` int(10) NOT NULL COMMENT '评分（0-5，星级评分）',
                                    `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间\r\n',
                                    `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `isDelete` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    UNIQUE INDEX `scoreId`(`commodityId`, `userId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1900781056169742339 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for commodity_type
-- ----------------------------
DROP TABLE IF EXISTS `commodity_type`;
CREATE TABLE `commodity_type`  (
                                   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品分类 ID',
                                   `typeName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '商品类别名称',
                                   `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `isDelete` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1900438197696618498 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
                           `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                           `noticeTitle` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '公告标题',
                           `noticeContent` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '公告内容',
                           `noticeAdminId` bigint(20) NOT NULL COMMENT '创建人id（管理员）',
                           `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           `isDelete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
                           PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for post
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post`  (
                         `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                         `title` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标题',
                         `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '内容',
                         `tags` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签列表（json 数组）',
                         `thumbNum` int(11) NOT NULL DEFAULT 0 COMMENT '点赞数',
                         `favourNum` int(11) NOT NULL DEFAULT 0 COMMENT '收藏数',
                         `userId` bigint(20) NOT NULL COMMENT '创建用户 id',
                         `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                         `isDelete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
                         PRIMARY KEY (`id`) USING BTREE,
                         INDEX `idx_userId`(`userId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1900447702241120258 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '帖子' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for post_favour
-- ----------------------------
DROP TABLE IF EXISTS `post_favour`;
CREATE TABLE `post_favour`  (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                                `postId` bigint(20) NOT NULL COMMENT '帖子 id',
                                `userId` bigint(20) NOT NULL COMMENT '创建用户 id',
                                `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                PRIMARY KEY (`id`) USING BTREE,
                                INDEX `idx_postId`(`postId`) USING BTREE,
                                INDEX `idx_userId`(`userId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 184 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '帖子收藏' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for post_thumb
-- ----------------------------
DROP TABLE IF EXISTS `post_thumb`;
CREATE TABLE `post_thumb`  (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                               `postId` bigint(20) NOT NULL COMMENT '帖子 id',
                               `userId` bigint(20) NOT NULL COMMENT '创建用户 id',
                               `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               PRIMARY KEY (`id`) USING BTREE,
                               INDEX `idx_postId`(`postId`) USING BTREE,
                               INDEX `idx_userId`(`userId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 178 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '帖子点赞' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for private_message
-- ----------------------------
DROP TABLE IF EXISTS `private_message`;
CREATE TABLE `private_message`  (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '消息 ID',
                                    `senderId` bigint(20) NOT NULL COMMENT '发送者 ID',
                                    `recipientId` bigint(20) NOT NULL COMMENT '接收者 ID',
                                    `content` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '消息内容(UTF8MB4 支持Emoji表情)',
                                    `alreadyRead` tinyint(4) NULL DEFAULT 0 COMMENT '0-未阅读 1-已阅读',
                                    `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '消息发送类型（用户发送还是管理员发送,user Or admin)枚举',
                                    `isRecalled` tinyint(4) NULL DEFAULT 0 COMMENT '是否撤回  0-未撤回 1-已撤回',
                                    `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `isDelete` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1901154100096696323 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
                         `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                         `userAccount` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号',
                         `userPassword` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
                         `unionId` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '微信开放平台id',
                         `mpOpenId` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '公众号openId',
                         `userName` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户昵称',
                         `userAvatar` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户头像',
                         `userProfile` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户简介',
                         `userRole` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'user' COMMENT '用户角色：user/admin/ban',
                         `userPhone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
                         `aiRemainNumber` int(11) NULL DEFAULT 0 COMMENT '用户 AI 剩余可使用次数',
                         `balance` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '用户余额（仅AI接口调用）',
                         `editTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '编辑时间',
                         `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                         `isDelete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
                         PRIMARY KEY (`id`) USING BTREE,
                         INDEX `idx_unionId`(`unionId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1890695308817182723 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_ai_message
-- ----------------------------
DROP TABLE IF EXISTS `user_ai_message`;
CREATE TABLE `user_ai_message`  (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                    `userInputText` varchar(4096) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户输入',
                                    `aiGenerateText` varchar(4096) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'AI 生成结果',
                                    `userId` bigint(20) NOT NULL COMMENT '用户ID',
                                    `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `isDelete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1901156016193175555 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_commodity_favorites
-- ----------------------------
DROP TABLE IF EXISTS `user_commodity_favorites`;
CREATE TABLE `user_commodity_favorites`  (
                                             `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                             `userId` bigint(20) NOT NULL COMMENT '用户 ID',
                                             `commodityId` bigint(20) NOT NULL COMMENT '商品 ID',
                                             `status` tinyint(4) NULL DEFAULT 1 COMMENT '1-正常收藏 0-取消收藏',
                                             `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户备注',
                                             `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                             `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                             `isDelete` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除',
                                             PRIMARY KEY (`id`) USING BTREE,
                                             UNIQUE INDEX `unique_favorite`(`userId`, `commodityId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1899732371092668419 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
