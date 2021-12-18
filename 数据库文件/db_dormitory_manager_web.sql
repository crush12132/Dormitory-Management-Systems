/*
Navicat MySQL Data Transfer

Source Server         : 本地数据库
Source Server Version : 50611
Source Host           : localhost:3306
Source Database       : db_dormitory_manager_web

Target Server Type    : MYSQL
Target Server Version : 50611
File Encoding         : 65001

Date: 2018-09-30 16:47:40
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `admin`
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  `password` varchar(32) NOT NULL,
  `status` int(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES ('1', 'admin', 'admin', '1');

-- ----------------------------
-- Table structure for `building`
-- ----------------------------
DROP TABLE IF EXISTS `building`;
CREATE TABLE `building` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `location` varchar(128) DEFAULT NULL,
  `dormitory_manager_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `dormitory_id_fk` (`dormitory_manager_id`),
  CONSTRAINT `dormitory_id_fk` FOREIGN KEY (`dormitory_manager_id`) REFERENCES `dormitory_manager` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of building
-- ----------------------------
INSERT INTO `building` VALUES ('1', '男生宿舍01栋', '体育馆西侧', '7');
INSERT INTO `building` VALUES ('2', '男生宿舍02栋', '教学楼北面', '18');
INSERT INTO `building` VALUES ('12', '女生宿舍01栋', '教学楼北侧', '18');

-- ----------------------------
-- Table structure for `dormitory`
-- ----------------------------
DROP TABLE IF EXISTS `dormitory`;
CREATE TABLE `dormitory` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sn` varchar(32) NOT NULL,
  `building_id` int(11) NOT NULL,
  `floor` varchar(32) NOT NULL,
  `max_number` int(2) NOT NULL,
  `lived_number` int(2) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `building_id_fk` (`building_id`),
  CONSTRAINT `building_id_fk` FOREIGN KEY (`building_id`) REFERENCES `building` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of dormitory
-- ----------------------------
INSERT INTO `dormitory` VALUES ('1', '男生宿舍01栋1001', '1', '一层', '4', '4');
INSERT INTO `dormitory` VALUES ('2', '男生宿舍02栋1003', '2', '一层', '4', '1');
INSERT INTO `dormitory` VALUES ('5', '男生宿舍01栋1002', '1', '一层', '4', '0');
INSERT INTO `dormitory` VALUES ('12', '女生宿舍01栋1001', '12', '一楼', '4', '0');

-- ----------------------------
-- Table structure for `dormitory_manager`
-- ----------------------------
DROP TABLE IF EXISTS `dormitory_manager`;
CREATE TABLE `dormitory_manager` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sn` varchar(32) NOT NULL,
  `name` varchar(32) NOT NULL,
  `password` varchar(32) NOT NULL,
  `sex` varchar(8) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of dormitory_manager
-- ----------------------------
INSERT INTO `dormitory_manager` VALUES ('7', 'S1537881909951', '张大爷', '123456', '男');
INSERT INTO `dormitory_manager` VALUES ('18', 'S1537965298839', '张阿姨', '123456', '女');

-- ----------------------------
-- Table structure for `live`
-- ----------------------------
DROP TABLE IF EXISTS `live`;
CREATE TABLE `live` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `student_id` int(11) NOT NULL,
  `dormitory_id` int(11) NOT NULL,
  `live_date` date NOT NULL,
  PRIMARY KEY (`id`),
  KEY `student_id_live_fk` (`student_id`),
  KEY `dormitory_id_live_fk` (`dormitory_id`),
  CONSTRAINT `dormitory_id_live_fk` FOREIGN KEY (`dormitory_id`) REFERENCES `dormitory` (`id`),
  CONSTRAINT `student_id_live_fk` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of live
-- ----------------------------
INSERT INTO `live` VALUES ('6', '7', '1', '2018-09-28');
INSERT INTO `live` VALUES ('7', '8', '1', '2018-09-29');
INSERT INTO `live` VALUES ('8', '18', '1', '2018-09-29');
INSERT INTO `live` VALUES ('9', '19', '1', '2018-09-29');
INSERT INTO `live` VALUES ('15', '20', '2', '2018-09-29');

-- ----------------------------
-- Table structure for `student`
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sn` varchar(32) NOT NULL,
  `name` varchar(32) NOT NULL,
  `password` varchar(32) NOT NULL,
  `sex` varchar(8) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of student
-- ----------------------------
INSERT INTO `student` VALUES ('7', 'S1537881909951', '张三', '123', '男');
INSERT INTO `student` VALUES ('8', 'S1537881928894', '李四', '123456', '男');
INSERT INTO `student` VALUES ('9', 'S1537882773788', '马冬梅', '123456789', '女');
INSERT INTO `student` VALUES ('18', 'S1538216365262', '晓明', '123', '男');
INSERT INTO `student` VALUES ('19', 'S1538216374036', '张军', '123456', '男');
INSERT INTO `student` VALUES ('20', 'S1538216388860', '黎明', '123456', '男');
INSERT INTO `student` VALUES ('21', 'S1538228077850', '王麻子', '123', '男');
