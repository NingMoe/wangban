-- Add/modify columns 
alter table PUB_CONTENT add RID_NAME VARCHAR2(200);
-- Add comments to the columns 
comment on column PUB_CONTENT.RID_NAME
  is '区划名称';

-- Add/modify columns 
alter table PUB_CHANNEL add PERMISSIONS VARCHAR2(4000);
-- Add comments to the columns 
comment on column PUB_CHANNEL.PERMISSIONS
  is '权限控制 存放部门code 用,隔开';
  
  -- Add/modify columns 
alter table PUB_CHANNEL add PER_FLAG CHAR(1);
-- Add comments to the columns 
comment on column PUB_CHANNEL.PER_FLAG
  is '权限控制 0根据PERMISSIONS控制 1全部具有权限';
  
  -- Add/modify columns 
alter table PUB_CHANNEL add PERMISSIONS_NAME VARCHAR2(4000);
-- Add comments to the columns 
comment on column PUB_CHANNEL.PERMISSIONS_NAME
  is '权限控制 存放部门名称 用,隔开';
  
  
  --企业设立并联审批相关表by wq 2015-8-26
  --start
create table ENTERPRISE_BUSINESS_INDEX
(
  id                 VARCHAR2(50) not null,
  receive_id         VARCHAR2(50),
  business_license   VARCHAR2(50),
  apply_subject      VARCHAR2(200),
  form_id            VARCHAR2(100) not null,
  form_name          VARCHAR2(200),
  data_id            VARCHAR2(50) not null,
  region_code        VARCHAR2(50),
  region_name        VARCHAR2(50),
  apply_time         DATE not null,
  accept_time        DATE,
  actual_finish_time DATE,
  applicant          VARCHAR2(100),
  enterprise_type    VARCHAR2(10) not null,
  org_actuality      VARCHAR2(20) not null,
  current_state      VARCHAR2(20) not null,
  first_grade_code   VARCHAR2(50),
  ucid               VARCHAR2(50),
  apply_data         CLOB
)

-- Add comments to the columns 
comment on column ENTERPRISE_BUSINESS_INDEX.id
  is '主键';
comment on column ENTERPRISE_BUSINESS_INDEX.receive_id
  is '申办编号';
comment on column ENTERPRISE_BUSINESS_INDEX.business_license
  is '工商注册号';
comment on column ENTERPRISE_BUSINESS_INDEX.apply_subject
  is '申办主题';
comment on column ENTERPRISE_BUSINESS_INDEX.form_id
  is '表单id';
comment on column ENTERPRISE_BUSINESS_INDEX.form_name
  is '表单名称';
comment on column ENTERPRISE_BUSINESS_INDEX.data_id
  is '表单数据id';
comment on column ENTERPRISE_BUSINESS_INDEX.region_code
  is '区划代码';
comment on column ENTERPRISE_BUSINESS_INDEX.region_name
  is '区划名称';
comment on column ENTERPRISE_BUSINESS_INDEX.apply_time
  is '申请时间';
comment on column ENTERPRISE_BUSINESS_INDEX.accept_time
  is '受理时间';
comment on column ENTERPRISE_BUSINESS_INDEX.actual_finish_time
  is '实际办结时间';
comment on column ENTERPRISE_BUSINESS_INDEX.applicant
  is '申请人';
comment on column ENTERPRISE_BUSINESS_INDEX.enterprise_type
  is '企业申报类型
1注册登记(内资)
2注册登记(外资)
3全流程注册登记(内资)
4全流程注册登记(外资)
';
comment on column ENTERPRISE_BUSINESS_INDEX.org_actuality
  is '业务类型
Register( 设立 ) 
Change( 变更 ) 
Deregister( 注销 ) 
Revoke (吊销)
';
comment on column ENTERPRISE_BUSINESS_INDEX.current_state
  is '当前办理状态 00暂存  01 提交  02预审通过  99  办结';
comment on column ENTERPRISE_BUSINESS_INDEX.first_grade_code
  is '一级码';
comment on column ENTERPRISE_BUSINESS_INDEX.ucid
  is '申请人id';
comment on column ENTERPRISE_BUSINESS_INDEX.apply_data
  is '申报数据';
  
  
  -- Create table
create table ENTERPRISE_BUSINESS_COURSE
(
  id                  VARCHAR2(50) not null,
  biz_id              VARCHAR2(50) not null,
  item_id             VARCHAR2(50) not null,
  item_code           VARCHAR2(50),
  item_name           VARCHAR2(200),
  org_code            VARCHAR2(50),
  org_name            VARCHAR2(50),
  receive_time        DATE,
  finish_time         DATE,
  parallel_stage_code VARCHAR2(20),
  handle_state        VARCHAR2(10) not null,
  region_code         VARCHAR2(50),
  region_name         VARCHAR2(50),
  second_grade_code   VARCHAR2(50),
  first_grade_code    VARCHAR2(50)
)

-- Add comments to the columns 
comment on column ENTERPRISE_BUSINESS_COURSE.biz_id
  is '实例ID,对应ENTERPRISE_BUSINESS_INDEX中的ID';
comment on column ENTERPRISE_BUSINESS_COURSE.receive_time
  is '接收时间';
comment on column ENTERPRISE_BUSINESS_COURSE.finish_time
  is '办结时间';
comment on column ENTERPRISE_BUSINESS_COURSE.handle_state
  is '办理状态：
未办10、在办11[已分发]、已办
99';
comment on column ENTERPRISE_BUSINESS_COURSE.second_grade_code
  is '二级码';
comment on column ENTERPRISE_BUSINESS_COURSE.first_grade_code
  is '一级码';

  --end
  
  --企业设立并联审批相关表by wq 2015-8-26
  --start
  
  -- Create table
create table PARALLEL_PROJECT
(
  id                      VARCHAR2(50) not null,
  project_id              VARCHAR2(20),
  project_name            VARCHAR2(200),
  government_investmet_id VARCHAR2(20),
  total_investment        NUMBER(16,5),
  investment_source       VARCHAR2(5),
  project_category        VARCHAR2(5),
  region                  VARCHAR2(20),
  construction_unit       VARCHAR2(200),
  registered_capital      NUMBER(16,5),
  unit_phone              VARCHAR2(20),
  unit_code               VARCHAR2(20),
  legal_man               VARCHAR2(20),
  contact_man             VARCHAR2(20),
  contact_man_id          VARCHAR2(18),
  contacts                VARCHAR2(20),
  year                    CHAR(4),
  serial_number           CHAR(4),
  create_time             DATE,
  land_area               NUMBER(16,5),
  project_address         VARCHAR2(200),
  unit_address            VARCHAR2(200),
  unit_in_short           VARCHAR2(20),
  updatedate              DATE default sysdate,
  first_code              VARCHAR2(30),
  ucid                    VARCHAR2(50)
)

-- Add comments to the table 
comment on table PARALLEL_PROJECT
  is '项目备案表';
-- Add comments to the columns 
comment on column PARALLEL_PROJECT.id
  is '主键';
comment on column PARALLEL_PROJECT.project_id
  is '项目编号';
comment on column PARALLEL_PROJECT.project_name
  is '项目名称';
comment on column PARALLEL_PROJECT.government_investmet_id
  is '政府投资项目编码';
comment on column PARALLEL_PROJECT.total_investment
  is '总投资额
单位：万元
';
comment on column PARALLEL_PROJECT.investment_source
  is '投资来源【字典】';
comment on column PARALLEL_PROJECT.project_category
  is '项目类别【字典】';
comment on column PARALLEL_PROJECT.region
  is '行政区划【字典】';
comment on column PARALLEL_PROJECT.construction_unit
  is '建设单位全称';
comment on column PARALLEL_PROJECT.registered_capital
  is '注册资金
单位：万元
';
comment on column PARALLEL_PROJECT.unit_phone
  is '单位电话';
comment on column PARALLEL_PROJECT.unit_code
  is '建设单位组织机构代码';
comment on column PARALLEL_PROJECT.legal_man
  is '法人代表';
comment on column PARALLEL_PROJECT.contact_man
  is '联系人';
comment on column PARALLEL_PROJECT.contact_man_id
  is '联系人身份证';
comment on column PARALLEL_PROJECT.contacts
  is '联系人联系方式';
comment on column PARALLEL_PROJECT.year
  is '年份';
comment on column PARALLEL_PROJECT.serial_number
  is '序列号';
comment on column PARALLEL_PROJECT.create_time
  is '创建时间';
comment on column PARALLEL_PROJECT.land_area
  is '用地面积 单位:平方米';
comment on column PARALLEL_PROJECT.project_address
  is '项目地址';
comment on column PARALLEL_PROJECT.unit_in_short
  is '行政区划简写';
comment on column PARALLEL_PROJECT.updatedate
  is '时间戳';
comment on column PARALLEL_PROJECT.first_code
  is '一码制一级编码';
comment on column PARALLEL_PROJECT.ucid
  is '申请人id';

	-- Create table
create table PARALLEL_BIZ_BASE
(
  biz_id             VARCHAR2(50) not null,
  apply_subject      VARCHAR2(200),
  apply_serial_no    VARCHAR2(20),
  project_id         VARCHAR2(20),
  project_biz_id     VARCHAR2(30),
  enterprises_id     VARCHAR2(50),
  enterprises_name   VARCHAR2(50),
  apply_time         DATE,
  accept_time        DATE,
  actual_finish_time DATE,
  leader_org_code    VARCHAR2(50),
  leader_org_name    VARCHAR2(50),
  flow_id            VARCHAR2(50),
  flow_name          VARCHAR2(100),
  stage_id           VARCHAR2(10),
  stage_name         VARCHAR2(50),
  form_id            VARCHAR2(100),
  data_id            VARCHAR2(50),
  biz_state          VARCHAR2(10),
  region_code        VARCHAR2(50),
  first_code         VARCHAR2(30),
  apply_data         CLOB,
  ucid               VARCHAR2(50),
  project_name       VARCHAR2(50)
)

-- Add comments to the table 
comment on table PARALLEL_BIZ_BASE
  is '业务主表';
-- Add comments to the columns 
comment on column PARALLEL_BIZ_BASE.biz_id
  is '主键，业务主键编号';
comment on column PARALLEL_BIZ_BASE.apply_subject
  is '业务主题';
comment on column PARALLEL_BIZ_BASE.apply_serial_no
  is '收件编号(null)';
comment on column PARALLEL_BIZ_BASE.project_id
  is '项目编号';
comment on column PARALLEL_BIZ_BASE.project_biz_id
  is '立案编号（null）';
comment on column PARALLEL_BIZ_BASE.enterprises_id
  is '企业编号（null）';
comment on column PARALLEL_BIZ_BASE.enterprises_name
  is '申请单位(人)名称';
comment on column PARALLEL_BIZ_BASE.apply_time
  is '申请时间';
comment on column PARALLEL_BIZ_BASE.accept_time
  is '预审通过时间';
comment on column PARALLEL_BIZ_BASE.actual_finish_time
  is '实际办结时间';
comment on column PARALLEL_BIZ_BASE.leader_org_code
  is '牵头部门编号（null）';
comment on column PARALLEL_BIZ_BASE.leader_org_name
  is '牵头部门名称（null）';
comment on column PARALLEL_BIZ_BASE.flow_id
  is '流程编号';
comment on column PARALLEL_BIZ_BASE.flow_name
  is '流程名称';
comment on column PARALLEL_BIZ_BASE.stage_id
  is '阶段编号';
comment on column PARALLEL_BIZ_BASE.stage_name
  is '阶段名称';
comment on column PARALLEL_BIZ_BASE.form_id
  is '表单编号';
comment on column PARALLEL_BIZ_BASE.data_id
  is '表单数据主键';
comment on column PARALLEL_BIZ_BASE.biz_state
  is '业务主状态:receive:收件
accept:预审通过
back:退件
correction:补齐不补正
finish:办结
';
comment on column PARALLEL_BIZ_BASE.region_code
  is '数据所属区域';
comment on column PARALLEL_BIZ_BASE.first_code
  is '一码制一级编码';
comment on column PARALLEL_BIZ_BASE.apply_data
  is '申请数据';
comment on column PARALLEL_BIZ_BASE.ucid
  is '申请人id';
comment on column PARALLEL_BIZ_BASE.project_name
  is '项目名称';
  
  -- Create table
create table ATTACH
(
  id    VARCHAR2(50) not null,
  type  VARCHAR2(10),
  conid VARCHAR2(50),
  name  VARCHAR2(50),
  yname VARCHAR2(100),
  docid VARCHAR2(10)
)
tablespace ICITY
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 8K
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table 
comment on table ATTACH
  is '附件表';
-- Add comments to the columns 
comment on column ATTACH.id
  is 'id';
comment on column ATTACH.type
  is '种类（新闻标题=1、新闻正文=2...)';
comment on column ATTACH.conid
  is '对应文章ID';
comment on column ATTACH.name
  is '附件名称（在服务器上的名称）';
comment on column ATTACH.yname
  is '上传源文件名称';
comment on column ATTACH.docid
  is '网盘文件ID';

--end
  
  
  
-- Create table start
-- liuyq  问卷调查后台模板
-- time 2015年09月15日 17:32
create table QUE_SURVEY
(
  id           VARCHAR2(255) not null,
  name         VARCHAR2(255),
  start_time   DATE,
  end_time     DATE,
  creater_id   VARCHAR2(255),
  creater_name VARCHAR2(255),
  dept         VARCHAR2(50),
  region       VARCHAR2(50),
  state        VARCHAR2(2),
  create_time  DATE,
  publish_time DATE,
  content      CLOB,
  type         VARCHAR2(10)
)
tablespace ICITY
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 8K
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table 
comment on table QUE_SURVEY
  is '问卷调查表';
-- Add comments to the columns 
comment on column QUE_SURVEY.name
  is '问卷名称';
comment on column QUE_SURVEY.start_time
  is '开始时间';
comment on column QUE_SURVEY.end_time
  is '结束时间';
comment on column QUE_SURVEY.creater_id
  is '创建人ID';
comment on column QUE_SURVEY.creater_name
  is '创建人名字';
comment on column QUE_SURVEY.dept
  is '部门ID';
comment on column QUE_SURVEY.region
  is '区划ID';
comment on column QUE_SURVEY.state
  is '状态   00：暂存    01：发布 ';
comment on column QUE_SURVEY.create_time
  is '创建时间';
comment on column QUE_SURVEY.publish_time
  is '发布时间';
comment on column QUE_SURVEY.content
  is '题目内容';
comment on column QUE_SURVEY.type
  is '类型       0:民意测评  1:网上调查 2:意见征集';
-- Create table end

  

-- Create table start
-- liuyq  问卷调查答案表
-- time 2015年09月15日 17:32
create table QUE_ANSWER
(
  id          VARCHAR2(255),
  survey_id   VARCHAR2(255),
  user_id     VARCHAR2(255),
  user_name   VARCHAR2(255),
  phone       VARCHAR2(20),
  ip          VARCHAR2(50),
  content     CLOB,
  survey_name VARCHAR2(255),
  submit_time DATE
)
tablespace ICITY
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64
    next 8
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table 
comment on table QUE_ANSWER
  is '问卷答案表';
-- Add comments to the columns 
comment on column QUE_ANSWER.id
  is '主键';
comment on column QUE_ANSWER.survey_id
  is '外键，管理问卷表';
comment on column QUE_ANSWER.user_id
  is '用户标识';
comment on column QUE_ANSWER.user_name
  is '用户名称';
comment on column QUE_ANSWER.phone
  is '电话号码';
comment on column QUE_ANSWER.ip
  is '用户提交答案ip地址';
comment on column QUE_ANSWER.content
  is '存储用户提交的答案';
comment on column QUE_ANSWER.survey_name
  is '问卷名称 ';
comment on column QUE_ANSWER.submit_time
  is '提交时间';
-- Create table end

  
-- Create table start
-- liuyq  问卷调查统计结果表
create table QUE_STATISTICS
(
  id        VARCHAR2(255),
  survey_id VARCHAR2(255),
  content   CLOB
)
tablespace ICITY
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table 
comment on table QUE_STATISTICS
  is '问卷统计表';
-- Add comments to the columns 
comment on column QUE_STATISTICS.id
  is '主键';
comment on column QUE_STATISTICS.survey_id
  is '问卷ID';
comment on column QUE_STATISTICS.content
  is '问卷统计结果';
-- Create table end

  
-- Create table start
-- liuyq  问卷调查统计结果表     问答题
-- time 2015年09月15日 17:32
create table QUE_ANSWER_OF_QUESTION
(
  id          VARCHAR2(50),
  survey_id   VARCHAR2(50),
  question_id VARCHAR2(50),
  title       VARCHAR2(1024),
  answer      CLOB,
  content     CLOB,
  answer_id   VARCHAR2(50)
)
tablespace ICITY
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64
    next 8
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table 
comment on table QUE_ANSWER_OF_QUESTION
  is '问答题答案表';
-- Add comments to the columns 
comment on column QUE_ANSWER_OF_QUESTION.id
  is '主键';
comment on column QUE_ANSWER_OF_QUESTION.survey_id
  is '问卷id';
comment on column QUE_ANSWER_OF_QUESTION.question_id
  is '问题id';
comment on column QUE_ANSWER_OF_QUESTION.title
  is '问题题目';
comment on column QUE_ANSWER_OF_QUESTION.answer
  is '问题回答';
comment on column QUE_ANSWER_OF_QUESTION.content
  is '问题大字段';
comment on column QUE_ANSWER_OF_QUESTION.answer_id
  is '关联答案表主键';
-- Create table end

-- Add/modify columns 
alter table GUESTBOOK add LY CHAR(1);
-- Add comments to the columns 
comment on column GUESTBOOK.LY
  is '来源(咨询、投诉)';
  
  
  
--舟山网上预约 非上班时间预约  panyl
create table FSBSJYY
(
  ID         VARCHAR2(100 CHAR) not null,
  YYDEPTNAME VARCHAR2(100 CHAR),
  USERNAME   VARCHAR2(200),
  YYDAY      VARCHAR2(22 CHAR),
  YYTIME     VARCHAR2(60 CHAR),
  YYRNAME    VARCHAR2(100 CHAR),
  YYRCARDNO  VARCHAR2(20 CHAR),
  YYCONTENT  VARCHAR2(1600 CHAR),
  YYRPHONE   VARCHAR2(18),
  DEPTPHONE  VARCHAR2(40 CHAR),
  CODE       VARCHAR2(20),
  REGIONCODE VARCHAR2(20),
  RE_CONTENT VARCHAR2(1600 CHAR),
  USERID     VARCHAR2(50),
  REDAY      DATE,
  RE_NAME    VARCHAR2(20),
  STATUS     VARCHAR2(2) default 0
)
tablespace ICITY
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
-- Add comments to the columns 
comment on column FSBSJYY.YYDEPTNAME
  is '预约部门名称';
comment on column FSBSJYY.USERNAME
  is '当前登录用户姓名';
comment on column FSBSJYY.YYDAY
  is '预约日期';
comment on column FSBSJYY.YYTIME
  is '预约时间';
comment on column FSBSJYY.YYRNAME
  is '预约人姓名';
comment on column FSBSJYY.YYRCARDNO
  is '预约人身份证号';
comment on column FSBSJYY.YYCONTENT
  is '预约内容说明';
comment on column FSBSJYY.YYRPHONE
  is '预约人电话';
comment on column FSBSJYY.DEPTPHONE
  is '部门电话';
comment on column FSBSJYY.CODE
  is '部门代码';
comment on column FSBSJYY.REGIONCODE
  is '部门行政区划号';
comment on column FSBSJYY.RE_CONTENT
  is '回复内容';
comment on column FSBSJYY.USERID
  is '用户id';
comment on column FSBSJYY.REDAY
  is '回复时间';
comment on column FSBSJYY.RE_NAME
  is '回复人';
comment on column FSBSJYY.STATUS
  is '回复状态';

----企业设立 ENTERPRISE_BUSINESS_COURSE 新增 联合踏勘标识与材料俩字段-----lhy------START
-- Add/modify columns 
alter table ENTERPRISE_BUSINESS_COURSE add IS_LHTK VARCHAR2(1);
-- Add comments to the columns 
comment on column ENTERPRISE_BUSINESS_COURSE.IS_LHTK
  is '是否联合踏勘 0否1是';
-- Add/modify columns 
alter table ENTERPRISE_BUSINESS_COURSE add MATERIAL CLOB;
-- Add comments to the columns 
comment on column ENTERPRISE_BUSINESS_COURSE.MATERIAL
  is '联合踏勘事项所需要提交的材料';
-----------------------------------------------------------------------------------END
  
----微信咨询投诉GUESTBOOK 新增 微信标识字段-----lk------START
-- Add/modify columns 
alter table GUESTBOOK add OPENID VARCHAR2(50);
-- Add comments to the columns 
comment on column GUESTBOOK.OPENID
  is '微信标识';
------------------------------------------------------END
  
----内容管理 PUB_CONTENT 新增 创建人ID、二级部门ID、二级部门名称-----lk----START
-- Add/modify columns 
alter table PUB_CONTENT add CREATOR_ID VARCHAR2(50);
-- Add comments to the columns 
comment on column PUB_CONTENT.CREATOR_ID
  is '创建人ID';

-- Add/modify columns 
alter table PUB_CONTENT add AGENT_CODE VARCHAR2(50);
-- Add comments to the columns 
comment on column PUB_CONTENT.AGENT_CODE
  is '二级部门（科室）ID';
  
-- Add/modify columns 
alter table PUB_CONTENT add AGENT_NAME VARCHAR2(50);
-- Add comments to the columns 
comment on column PUB_CONTENT.AGENT_NAME
  is '二级部门（科室）名称';
-------------------------------------------------------------------------END
---2015.10.15
  -- Add comments to the columns 
comment on column GUESTBOOK.type
  is '信件类别，分为咨询-2、投诉-3、意见建议-10、纠错-11、求助-12、其他';
comment on column GUESTBOOK.status
  is '状态，0-未办理、1-已回复、3-审核通过、4-审核不通过，默认为未办理';
  

  ----昆明督办回复表--------------------------------------------------------lhy--START
  -- Create table
create table SUPERVISION
(
  ID                VARCHAR2(100) not null,
  GID               VARCHAR2(100),
  XZSPSUPERVISIOID  VARCHAR2(100),
  CONTENT           CLOB,
  SUPERVISIONDEP    VARCHAR2(100),
  SUPERVISIONPERSON VARCHAR2(50),
  SUPERVISIONDATE   DATE,
  REPLYSTATUS       VARCHAR2(1),
  REPLYDATE         DATE,
  REPLYDEP          VARCHAR2(100),
  REPLYPERSON       VARCHAR2(100)
)
tablespace ICITY
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64
    next 1
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table 
comment on table SUPERVISION
  is '昆明，督办回复表';
-- Add comments to the columns 
comment on column SUPERVISION.GID
  is 'guestbook表的id 投诉编号';
comment on column SUPERVISION.XZSPSUPERVISIOID
  is '督办编号（新锐和达）';
comment on column SUPERVISION.CONTENT
  is '督办回复内容';
comment on column SUPERVISION.SUPERVISIONDEP
  is '督办部门';
comment on column SUPERVISION.SUPERVISIONPERSON
  is '督办人';
comment on column SUPERVISION.SUPERVISIONDATE
  is '最新督办时间';
comment on column SUPERVISION.REPLYSTATUS
  is '督办回复状态，0为督办 1为回复';
comment on column SUPERVISION.REPLYDATE
  is '最新回复时间';
comment on column SUPERVISION.REPLYDEP
  is '回复部门';
comment on column SUPERVISION.REPLYPERSON
  is '回复人';
-------------------------------------------------------------------------END
----企业设立 ENTERPRISE_BUSINESS_COURSE 新增 提交材料字段-----lk------START
-- Add/modify columns 
alter table ENTERPRISE_BUSINESS_COURSE add APPLY_DATA CLOB;
-- Add comments to the columns 
comment on column ENTERPRISE_BUSINESS_COURSE.APPLY_DATA
  is '提交的材料';
----------------------------------------------------------------------END

----满意度评价 STAR_LEVEL_EVALUATION 新增 字段-----lk------START
-- Add/modify columns 
alter table STAR_LEVEL_EVALUATION add SERVICE_ORG_NAME VARCHAR2(50);
-- Add comments to the columns 
comment on column STAR_LEVEL_EVALUATION.SERVICE_ORG_NAME
  is '事项主管部门组织机构名称';
-- Add/modify columns 
alter table STAR_LEVEL_EVALUATION add REGION_ID VARCHAR2(50);
-- Add comments to the columns 
comment on column STAR_LEVEL_EVALUATION.REGION_ID
  is '行政区划ID';
-- Add/modify columns 
alter table STAR_LEVEL_EVALUATION add REGION_NAME VARCHAR2(50);
-- Add comments to the columns 
comment on column STAR_LEVEL_EVALUATION.REGION_NAME
  is '行政区划名称';
----------------------------------------------------------END

---------栏目新增WEBSITETYPE-----lhy------START
  -- Add/modify columns 
alter table PUB_CHANNEL add WEBSITETYPE VARCHAR2(1) default 0;
-- Add comments to the columns 
comment on column PUB_CHANNEL.WEBSITETYPE
  is '网站类型，0外网1审招委2内门户3公共  舟山一套程序包含多套网站';
----------------------------------------------------------END
-- Add/modify columns 
alter table STAR_LEVEL_EVALUATION add service_id VARCHAR2(50);
alter table STAR_LEVEL_EVALUATION add service_org_name VARCHAR2(50);
alter table STAR_LEVEL_EVALUATION add apply_name VARCHAR2(100);
alter table STAR_LEVEL_EVALUATION add region_code VARCHAR2(100);
alter table STAR_LEVEL_EVALUATION add service_name VARCHAR2(100);

-- Add comments to the columns 2015-10-27 by wq
comment on column STAR_LEVEL_EVALUATION.creator_id
  is '如果有登录，则直接用网办账户';
comment on column STAR_LEVEL_EVALUATION.service_id
  is '服务事项id';
comment on column STAR_LEVEL_EVALUATION.apply_name
  is '申报主题';
comment on column STAR_LEVEL_EVALUATION.service_name
  is '服务事项名称';
  --2015-11-4 by wq
  CREATE TABLE wechat_user  ( 
    ACCESSTIME  DATE NULL,
    STATUS      VARCHAR2(30) NULL,
    OPENID      VARCHAR2(30) NULL,
    USERID      VARCHAR2(30) NULL,
    LOGINNAME   VARCHAR2(30) NULL,
    ID          VARCHAR2(32) NOT NULL 
    )
    --2015-11-6 by wq
    -- Add/modify columns 
alter table BUSINESS_INDEX add bzsx number;
-- Add comments to the columns 
comment on column BUSINESS_INDEX.bzsx
  is '补正时限';
  	--2015-11-17 by linyong
    -- Add/modify columns
alter table POWER_BASE_INFO_FAVORITE modify id VARCHAR2(50);

------------附件表-lhy-2015-11-18-------------------------------------------------------------
-- Create table
create table BUSINESS_ATTACH
(
  ID         VARCHAR2(50) not null,
  TYPE       VARCHAR2(10),
  SBLSH      CLOB,
  NAME       VARCHAR2(50),
  YNAME      VARCHAR2(100),
  DOCID      VARCHAR2(50),
  UCID       VARCHAR2(50),
  UPLOADTIME VARCHAR2(50),
  REMARK     VARCHAR2(500),
  STATE      VARCHAR2(1)
)
tablespace ICITY
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 16
    next 8
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table 
comment on table BUSINESS_ATTACH
  is '附件表';
-- Add comments to the columns 
comment on column BUSINESS_ATTACH.ID
  is 'ID主键';
comment on column BUSINESS_ATTACH.TYPE
  is '0在线办理提交1业务中心提交';
comment on column BUSINESS_ATTACH.SBLSH
  is '业务关联申办流水号集合';
comment on column BUSINESS_ATTACH.NAME
  is '附件名称（在服务器上的名称）';
comment on column BUSINESS_ATTACH.YNAME
  is '上传源文件名称';
comment on column BUSINESS_ATTACH.DOCID
  is '网盘文件ID';
comment on column BUSINESS_ATTACH.UCID
  is '用户ID';
comment on column BUSINESS_ATTACH.UPLOADTIME
  is '上传日期';
comment on column BUSINESS_ATTACH.REMARK
  is '备注';
comment on column BUSINESS_ATTACH.STATE
  is '0可删除1不可删除';
-------------------------------------------------------------------------------------------end----
--清除无用的表 by wq 2015-11-23
drop table power_base_info;
drop table power_base_info_appoint;
drop table power_base_info_correct;
drop table power_base_info_of_form;
drop table power_base_info_jdsq;
drop table power_base_info_topic;
drop table power_base_info_ws;
drop table pub_organ;
drop table pub_region;
drop table service_institution_baseinfo;
drop table service_institution_contact;
drop table service_institution_middle;
drop table service_institution_photo;
drop table service_institution_traffic;
drop table service_institution_worktime;
drop table wiki_topic;
drop table base_lawinfo;
drop table base_window;
drop table business_appoint;
drop table ex_gdbs_sb;
drop table ex_gdbs_sbcl;
drop table ex_gdbs_wscx;
drop table index_test;

--网办管理增加事项信息是否数据对接标识    by：duanfakai 2015.11.30
create table APPROVE_ITEM_WORKFLOW_FORM
(
  ID                 VARCHAR2(50) not null,
  ITEM_ID            VARCHAR2(50) not null,
  WORKFLOW_ID        VARCHAR2(64),
  MODEL_ID           VARCHAR2(64),
  FORM_ID            VARCHAR2(64),
  FORM_NAME          VARCHAR2(200),
  FIRSTPOST          VARCHAR2(100),
  FIRSTROLE          VARCHAR2(100),
  FIRSTUSER          VARCHAR2(500),
  FORM_FIELD_SETTING CLOB
);
-- Add comments to the table 
comment on table APPROVE_ITEM_WORKFLOW_FORM
  is '事项流程表单关系表';
-- Add comments to the columns 
comment on column APPROVE_ITEM_WORKFLOW_FORM.ID
  is '主键';
comment on column APPROVE_ITEM_WORKFLOW_FORM.ITEM_ID
  is '事项ID';
comment on column APPROVE_ITEM_WORKFLOW_FORM.WORKFLOW_ID
  is '流程定义ID';
comment on column APPROVE_ITEM_WORKFLOW_FORM.MODEL_ID
  is '流程模型Id';
comment on column APPROVE_ITEM_WORKFLOW_FORM.FORM_ID
  is '表单ID';
comment on column APPROVE_ITEM_WORKFLOW_FORM.FORM_NAME
  is '表单名称';
comment on column APPROVE_ITEM_WORKFLOW_FORM.FIRSTPOST
  is '流程首环节岗位';
comment on column APPROVE_ITEM_WORKFLOW_FORM.FIRSTROLE
  is '流程首环节角色';
comment on column APPROVE_ITEM_WORKFLOW_FORM.FIRSTUSER
  is '流程首环节用户';
comment on column APPROVE_ITEM_WORKFLOW_FORM.FORM_FIELD_SETTING
  is '设置表单域（外网）';
  ------
  create table APPROVE_FORM_BASE_MAPPING_INFO
(
  ID                VARCHAR2(32) not null,
  MAPPING_ID        VARCHAR2(50) not null,
  SOURCE_FIELD_ID   VARCHAR2(50) not null,
  SOURCE_FILED_NAME VARCHAR2(50) not null,
  TARGET_FIELD_ID   VARCHAR2(50) not null,
  TARGET_FIELD_NAME VARCHAR2(50) not null,
  REMARK            VARCHAR2(500)
);

-- Add comments to the table 
comment on table APPROVE_FORM_BASE_MAPPING_INFO
  is '表单映射域信息表';
-- Add comments to the columns 
comment on column APPROVE_FORM_BASE_MAPPING_INFO.MAPPING_ID
  is 'APPROVE_FORM_BASE_MAPPING表的主键';
comment on column APPROVE_FORM_BASE_MAPPING_INFO.SOURCE_FIELD_ID
  is '源表单域ID';
comment on column APPROVE_FORM_BASE_MAPPING_INFO.SOURCE_FILED_NAME
  is '源表单域名称';
comment on column APPROVE_FORM_BASE_MAPPING_INFO.TARGET_FIELD_ID
  is '目标基础信息ID';
comment on column APPROVE_FORM_BASE_MAPPING_INFO.TARGET_FIELD_NAME
  is '目标基础信息名称';
comment on column APPROVE_FORM_BASE_MAPPING_INFO.REMARK
  is '备注说明';
--------
create table APPROVE_FORM_BASE_MAPPING
(
  MAPPING_ID       VARCHAR2(32) not null,
  MAPPING_NAME     VARCHAR2(100) not null,
  USER_CODE        VARCHAR2(50),
  USER_NAME        VARCHAR2(50),
  ORG_CODE         VARCHAR2(50),
  ORG_NAME         VARCHAR2(50),
  SOURCE_FORM_ID   VARCHAR2(100) not null,
  SOURCE_FORM_NAME VARCHAR2(100) not null,
  TARGET_BASE_TYPE VARCHAR2(1) not null,
  CREATE_TIME      DATE not null
);

-- Add comments to the table 
comment on table APPROVE_FORM_BASE_MAPPING
  is '表单与基本信息映射表';
-- Add comments to the columns 
comment on column APPROVE_FORM_BASE_MAPPING.MAPPING_ID
  is '主键';
comment on column APPROVE_FORM_BASE_MAPPING.MAPPING_NAME
  is '映射名称';
comment on column APPROVE_FORM_BASE_MAPPING.USER_CODE
  is '创建人ID';
comment on column APPROVE_FORM_BASE_MAPPING.USER_NAME
  is '创建人姓名';
comment on column APPROVE_FORM_BASE_MAPPING.ORG_CODE
  is '创建单位ID';
comment on column APPROVE_FORM_BASE_MAPPING.ORG_NAME
  is '创建单位名称';
comment on column APPROVE_FORM_BASE_MAPPING.SOURCE_FORM_ID
  is '源表单Id';
comment on column APPROVE_FORM_BASE_MAPPING.SOURCE_FORM_NAME
  is '源表单名称';
comment on column APPROVE_FORM_BASE_MAPPING.TARGET_BASE_TYPE
  is '目标业务对象类型';
comment on column APPROVE_FORM_BASE_MAPPING.CREATE_TIME
  is '创建日期';
  -- Create table
create table APPROVE_NODE_CONFIG
(
  ID                 VARCHAR2(50) not null,
  ITEM_ID            VARCHAR2(50) not null,
  WORKFLOW_ID        VARCHAR2(64),
  WORKFLOW_NODE_ID   VARCHAR2(50),
  FORM_ID            VARCHAR2(50),
  LINK_WARNING_TIME  VARCHAR2(10),
  LINK_TIME          VARCHAR2(10),
  FORM_NAME          VARCHAR2(200),
  SUSPEND_TYPE       VARCHAR2(200),
  SUSPEND_LIMIT_TIME VARCHAR2(10),
  IS_SUSPEND_APPLY   VARCHAR2(1)
);

-- Add comments to the table 
comment on table APPROVE_NODE_CONFIG
  is '事项流程节点配置表';
-- Add comments to the columns 
comment on column APPROVE_NODE_CONFIG.FORM_ID
  is '该环节的表单 ID，可以为空  如果为空为
APPROVE_ ITEM_
WORKFLOW_FORM
行政审批系统数据结构设计
第 29 页
表中配置的表单ID';
comment on column APPROVE_NODE_CONFIG.FORM_NAME
  is '表单名称';
comment on column APPROVE_NODE_CONFIG.SUSPEND_TYPE
  is '挂起类型';
comment on column APPROVE_NODE_CONFIG.SUSPEND_LIMIT_TIME
  is '挂起时限';
comment on column APPROVE_NODE_CONFIG.IS_SUSPEND_APPLY
  is '是否挂起审核 0：否，1：是';
  ---------------
  -- Create table
create table APPROVE_ITEM_EXT
(
  ID                  VARCHAR2(32) not null,
  ITEM_ID             VARCHAR2(50) not null,
  IS_HALL_COLLECTION  CHAR(1),
  SERVICE_OBJECT_TYPE VARCHAR2(50),
  ITEM_WARNING_TIME   VARCHAR2(10),
  ITEM_TIME           NUMBER,
  ORGAN_CODE          VARCHAR2(32),
  ORGAN_NAME          VARCHAR2(200),
  REGION_CODE         VARCHAR2(32),
  REGION_NAME         VARCHAR2(200),
  PROJECT_TYPE        VARCHAR2(20),
  IS_IMPORTANT        CHAR(1),
  IS_SETUP            VARCHAR2(10) default 0,
  ITEM_CODE           VARCHAR2(200),
  ITEM_NAME           VARCHAR2(1000),
  ASSORT              CHAR(1),
  AGENT_NAME          VARCHAR2(100),
  ITEM_TYPE           VARCHAR2(10),
  AGENT_CODE          VARCHAR2(50),
  ITEM_OBJECT         VARCHAR2(1000)
);

-- Add comments to the table 
comment on table APPROVE_ITEM_EXT
  is '事项扩展信息表';
-- Add comments to the columns 
comment on column APPROVE_ITEM_EXT.ID
  is '流水Id主键 不能为空';
comment on column APPROVE_ITEM_EXT.ITEM_ID
  is '事项ID不能为空 事项系统PROJECT_ITEM表ID';
comment on column APPROVE_ITEM_EXT.IS_HALL_COLLECTION
  is '是否大厅收件';
comment on column APPROVE_ITEM_EXT.SERVICE_OBJECT_TYPE
  is '事项服务对象类型';
comment on column APPROVE_ITEM_EXT.ITEM_WARNING_TIME
  is '事项预警时限';
comment on column APPROVE_ITEM_EXT.ITEM_TIME
  is '事项时限';
comment on column APPROVE_ITEM_EXT.ORGAN_CODE
  is '事项所属单位CODE';
comment on column APPROVE_ITEM_EXT.ORGAN_NAME
  is '事项所属单位名称';
comment on column APPROVE_ITEM_EXT.REGION_CODE
  is '事项所属区划CODE';
comment on column APPROVE_ITEM_EXT.REGION_NAME
  is '事项所属区划名称';
comment on column APPROVE_ITEM_EXT.PROJECT_TYPE
  is '项目类别';
comment on column APPROVE_ITEM_EXT.IS_IMPORTANT
  is '是否重点项目';
comment on column APPROVE_ITEM_EXT.IS_SETUP
  is '企业设立：1是 ；0否';
comment on column APPROVE_ITEM_EXT.ITEM_CODE
  is '事项CODE';

--重庆星级评价添加字段 by：duanfakai
  alter table STAR_LEVEL_EVALUATION add QUALITY_STAR_LEVEL number;
-- Add comments to the columns 
comment on column STAR_LEVEL_EVALUATION.QUALITY_STAR_LEVEL
  is '服务质量评价';
  
  alter table STAR_LEVEL_EVALUATION add TIME_STAR_LEVEL number;
-- Add comments to the columns 
comment on column STAR_LEVEL_EVALUATION.TIME_STAR_LEVEL
  is '办件时间评价';
  
  alter table STAR_LEVEL_EVALUATION add MAJOR_STAR_LEVEL number;
-- Add comments to the columns 
comment on column STAR_LEVEL_EVALUATION.MAJOR_STAR_LEVEL
  is '业务专业评价';
  
  
  ------------投资审批报告上传------lhy------START
  -- Create table
create table IPRO_REPORT
(
  ID                VARCHAR2(50) not null,
  PROJECTCODE       VARCHAR2(50),
  PROJECTSTATUSID   VARCHAR2(10),
  PROJECTSTATUSNAME VARCHAR2(50),
  NODENAME          VARCHAR2(50),
  METAIL            CLOB,
  INSERTTIME        DATE,
  UUID              VARCHAR2(50),
  NODEID	VARCHAR2(50),
  PROJECTNAME	VARCHAR2(100)
)
-- Add comments to the table 
comment on table IPRO_REPORT
  is '投资审批报告上传表';
-- Add comments to the columns 
comment on column IPRO_REPORT.ID
  is 'id';
comment on column IPRO_REPORT.PROJECTCODE
  is '项目编码对应ipro_index';
comment on column IPRO_REPORT.PROJECTSTATUSID
  is '项目状态（字典属性）0在建1竣工2撤销';
comment on column IPRO_REPORT.PROJECTSTATUSNAME
  is '项目状态名称（字典名称）';
comment on column IPRO_REPORT.NODENAME
  is '节点名称（字典名称）';
comment on column IPRO_REPORT.METAIL
  is '附件';
comment on column IPRO_REPORT.INSERTTIME
  is '时间';
comment on column IPRO_REPORT.UUID
  is '用户id';
 comment on column IPRO_REPORT.NODEID
  is '节点代码';
 comment on column IPRO_REPORT.PROJECTNAME
  is '项目名称';
--------------------------------------------------------------------end----
-- Create table
create table IPRO_INDEX
(
  SBLSH        VARCHAR2(50),
  SXBM         VARCHAR2(50),
  SXMC         VARCHAR2(1000),
  UUID         VARCHAR2(50),
  SBSJ         DATE,
  CONTENT      CLOB,
  UCNAME       VARCHAR2(50),
  REGION_CODE  VARCHAR2(50),
  REGION_NAME  VARCHAR2(50),
  PROJECTNAME  VARCHAR2(1000),
  SEQID        VARCHAR2(50),
  PROJECT_TYPE VARCHAR2(10)
)
-- Add comments to the columns 
comment on column IPRO_INDEX.SBLSH
  is '申报编号（申报提交时由投资审批接口返回）';
comment on column IPRO_INDEX.SXBM
  is '目录编码';
comment on column IPRO_INDEX.SXMC
  is '目录名称';
comment on column IPRO_INDEX.UUID
  is '用户唯一标识';
comment on column IPRO_INDEX.SBSJ
  is '申报时间';
comment on column IPRO_INDEX.CONTENT
  is '在线填报表单内容';
comment on column IPRO_INDEX.UCNAME
  is '用户名称';
comment on column IPRO_INDEX.REGION_CODE
  is '区域编码';
comment on column IPRO_INDEX.REGION_NAME
  is '区域名称';
comment on column IPRO_INDEX.PROJECTNAME
  is '项目名称';
comment on column IPRO_INDEX.SEQID
  is 'seqId（申报提交时由投资审批接口返回）';
comment on column IPRO_INDEX.PROJECT_TYPE
  is '项目类型 审核 。。。等';

  
  ------------------lhy-2015-12-5---
      -- Add/modify columns 
alter table IPRO_INDEX add PRINTCONTENT CLOB;
-- Add comments to the columns 
comment on column IPRO_INDEX.PRINTCONTENT
  is '打印数据';
  
    ------------------lk-2015-12-18---
-- Add/modify columns 
alter table PUB_CONTENT add POWER_TYPE VARCHAR2(50);
-- Add comments to the columns 
comment on column PUB_CONTENT.POWER_TYPE
  is '权力类型';
  
--start----duanfakai-2015-12-19----start---
 -- Add/modify columns 
alter table GUESTBOOK add DESIGN VARCHAR2(50); 
 -- Add/modify columns 
alter table GUESTBOOK add PLEASE VARCHAR2(50); 
  
comment on column GUESTBOOK.DESIGN
  is '网站整体设计评价';
comment on column GUESTBOOK.PLEASE
  is '网站办事方便程度评价';
--end-----duanfakai-2015-12-19-----end---
  
--start----liukang-2015-12-20----start---
 -- Add/modify columns 
alter table GUESTBOOK add ZJHM VARCHAR2(50); 
  
comment on column GUESTBOOK.ZJHM
  is '证件号码';
--end-----liukang-2015-12-20-----end---
-- Add/modify columns--2015-12-22 
alter table PRE_APASINFO rename column is_tongjian to power_source;
alter table PRE_FILE rename column is_tongjian to power_source;
alter table PRE_COMM_FORM rename column is_tongjian to power_source;
alter table PRE_JSON_DATA rename column is_tongjian to power_source;
-- Add comments to the columns 
comment on column PRE_FILE.power_source
  is '接入等级ABC';
  comment on column PRE_APASINFO.power_source
  is '接入等级ABC';
   comment on column PRE_COMM_FORM.power_source
  is '接入等级ABC';
  comment on column PRE_JSON_DATA.power_source
  is '接入等级ABC';
  -- Add/modify columns 
alter table PRE_APASINFO modify deptid VARCHAR2(50);
alter table PRE_APASINFO modify region_id VARCHAR2(30);
alter table PRE_COMM_FORM modify projid VARCHAR2(50);
alter table PRE_COMM_FORM modify deptid VARCHAR2(50);
alter table PRE_FILE modify deptid VARCHAR2(50);

---------lhy---2015-12-25------Start---
alert table GUESTBOOK add POWERTYPE VARCHAR2(100);
comment on column GUESTBOOK.POWERTYPE
  is '烟台项目新增 权利类型（行政审批，行政处罚）用户提交的时候自由选择';
-----------------------------End--------
  
  -- Create table by wq 2015-12-26
-- Create table
drop table PUB_HITS;
commit;
Create table PUB_HITS
(
  id            VARCHAR2(50),
  webname     	VARCHAR2(50),
  webregion     VARCHAR2(50),
  webrank     	VARCHAR2(50),
  catalog       VARCHAR2(50),
  visitedtitle  VARCHAR2(100),
  visitedurl    VARCHAR2(200),
  ip     		VARCHAR2(50),
  browser   	VARCHAR2(200),
  visittime     DATE,
  userid 		VARCHAR2(50),
  appid 		VARCHAR2(20)
);
commit;
-- Add comments to the columns 
comment on column PUB_HITS.id
  is '流水号';
  comment on column PUB_HITS.webname
  is '分厅名称';
comment on column PUB_HITS.webregion
  is '分厅区划';
  comment on column PUB_HITS.webrank
  is '行政级别';
comment on column PUB_HITS.catalog
  is '按目录分类：0-网站访问流量、1-网站内容关注度';
comment on column PUB_HITS.visitedtitle
  is '被访问资源名称';
comment on column PUB_HITS.visitedurl
  is '被访问资源url';
comment on column PUB_HITS.ip
  is '访问者ip';
comment on column PUB_HITS.browser
  is '访问者浏览器信息';
comment on column PUB_HITS.visittime
  is '访问时间';
comment on column PUB_HITS.userid
  is '访问者登录id';
  comment on column PUB_HITS.appid
  is '被访问系统标识';

---------lk---2015-12-30------Start---
alter table STAR_LEVEL_EVALUATION add EVALUATE_TYPE CHAR(1);
comment on column STAR_LEVEL_EVALUATION.EVALUATE_TYPE
  is '评价类型 1办件评价 2部门评价';
-----------------------------End--------

------------2016-1-6------Start--lhy-
alter table POWER_CATALOG add IS_USE CHAR(1);
comment on column POWER_CATALOG.IS_USE
  is '是否启用：1—是；2—否';
-----------------------------End--------
  
---------lk---2016-1-6------Start---
create table PUB_MSG_CONFIG
(
  id          VARCHAR2(50) not null,
  region_code VARCHAR2(50),
  region_name VARCHAR2(50),
  organ_code  VARCHAR2(50),
  organ_name  VARCHAR2(50),
  person_code VARCHAR2(50),
  person_name VARCHAR2(50),
  type_code   VARCHAR2(10),
  type_name   VARCHAR2(50),
  phone       VARCHAR2(50),
  create_time DATE
)
tablespace ICITY
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 16
    next 8
    minextents 1
    maxextents unlimited
  );
-- Add comments to the columns 
comment on column PUB_MSG_CONFIG.id
  is '主键';
comment on column PUB_MSG_CONFIG.region_code
  is '区划code';
comment on column PUB_MSG_CONFIG.region_name
  is '区划名称';
comment on column PUB_MSG_CONFIG.organ_code
  is '部门code';
comment on column PUB_MSG_CONFIG.organ_name
  is '部门名称';
comment on column PUB_MSG_CONFIG.person_code
  is '人员code';
comment on column PUB_MSG_CONFIG.person_name
  is '人员名称';
comment on column PUB_MSG_CONFIG.type_code
  is '类型code 咨询-2、投诉-3、外网申报-20、非上班时间预约-21、意见建议-10、纠错-11';
comment on column PUB_MSG_CONFIG.type_name
  is '类型名称';
comment on column PUB_MSG_CONFIG.phone
  is '手机号码';
comment on column PUB_MSG_CONFIG.create_time
  is '创建时间';
-----------------------------End--------

----------------2016-1-6---lhy---start-----
-- Create table
create table WEBSITE_HOTSERVICE
(
  ID              VARCHAR2(50) not null,
  TYPE            VARCHAR2(50),
  HOTSERVICE_NAME VARCHAR2(200),
  HOTSERVICE_URL  VARCHAR2(100),
  ITEM_ID         VARCHAR2(50),
  ITEM_CODE       VARCHAR2(100),
  ITEM_NAME       VARCHAR2(200),
  ORG_CODE        VARCHAR2(100),
  ORG_NAME        VARCHAR2(200)
)
-- Add comments to the table 
comment on table WEBSITE_HOTSERVICE
  is '热门服务发布';
-- Add comments to the columns 
comment on column WEBSITE_HOTSERVICE.ID
  is 'String类型id';
comment on column WEBSITE_HOTSERVICE.TYPE
  is '热门服务类型';
comment on column WEBSITE_HOTSERVICE.HOTSERVICE_NAME
  is '热门服务名称';
comment on column WEBSITE_HOTSERVICE.HOTSERVICE_URL
  is '链接';
comment on column WEBSITE_HOTSERVICE.ITEM_ID
  is '事项id';
comment on column WEBSITE_HOTSERVICE.ITEM_CODE
  is '事项编码';
comment on column WEBSITE_HOTSERVICE.ITEM_NAME
  is '事项名称';
comment on column WEBSITE_HOTSERVICE.ORG_CODE
  is '事项部门编码';
comment on column WEBSITE_HOTSERVICE.ORG_NAME
  is '事项部门名称';
  ---------------------------------end-------------------
-- Create table  2016-1-13 by wq
create table PUB_SMS
(
  id         VARCHAR2(50) not null,
  smscontent VARCHAR2(2000),
  sendtime   DATE,
  channel    VARCHAR2(50),
  status     CHAR(1),
  updatetime DATE,
  telephone  VARCHAR2(100),
  rid        VARCHAR2(30),
  result     VARCHAR2(2000),
  extcode    VARCHAR2(7)
);
commit;
comment on table PUB_SMS
  is '短信信息表';
comment on column PUB_SMS.id
  is '短信编号';
comment on column PUB_SMS.smscontent
  is '短信内容';
comment on column PUB_SMS.sendtime
  is '发送时间';
comment on column PUB_SMS.channel
  is '来自栏目（register：注册、notice：通知或告知、complain：投诉、consult：咨询，：others：其他）';
comment on column PUB_SMS.status
  is '发送状态(0：未操作，1：发送成功，2：发送失败，3：失败次数超过3次，不再推送)';
comment on column PUB_SMS.updatetime
  is '状态更新时间';
comment on column PUB_SMS.telephone
  is '发送号码';
comment on column PUB_SMS.rid
  is '行政区划';
comment on column PUB_SMS.result
  is '短信回复';
comment on column PUB_SMS.extcode
  is '短信标识';
  
------------2016-1-14------Start--lk-
alter table BUSINESS_INDEX add PAYSTATUS VARCHAR2(2);
comment on column BUSINESS_INDEX.PAYSTATUS
  is '缴费状态 0 默认 1未缴费 2 已缴费';
alter table BUSINESS_INDEX add PAYCONTENT CLOB;
comment on column BUSINESS_INDEX.PAYCONTENT
  is '缴费内容';
----------------------------End--------

------------2016-1-15------Start--lhy-----
alter table PUB_CONTENT add POWER_TYPE_ID NUMBER;
comment on column PUB_CONTENT.POWER_TYPE_ID
  is '权力类型代码  排序用';
----------------------------End--------
  ------------2016-1-22------Start--wq-----
-- Add/modify columns 
alter table BUSINESS_INDEX add signstate VARCHAR2(1) default 0;
-- Add comments to the columns 
comment on column BUSINESS_INDEX.signstate
  is '数据交换标志位0不同步1未同步2已同步';
----------------------------End--------
--清除无用的表 by wq 2016-2-18
drop table business_serialnum;
drop table business_serialnum_log;

------------2016-2-22------Start--lhy-----
alter table GUESTBOOK add COMMITMENTTIME DATE;
comment on column GUESTBOOK.COMMITMENTTIME
  is '承诺办理时间';
  alter table GUESTBOOK add COMMITMENT VARCHAR2(10);
comment on column GUESTBOOK.COMMITMENT
  is '承诺时限 如无设置默认为3个工作日';
----------------------------End--------

  ------------2016-2-23------Start--dfk-----
  -- Create table
create table UC_USER_EXT
(
  ID              NUMBER,
  SEX             VARCHAR2(12),
  NATIVEPLACE     VARCHAR2(200),
  NATION          VARCHAR2(32),
  BIRTHDAY        VARCHAR2(64),
  EDUCATION       VARCHAR2(32),
  POLITICALSTATUS VARCHAR2(32),
  HOMEADDRESS     VARCHAR2(200),
  POSTCODE        VARCHAR2(32)
)
-- Add comments to the table 
comment on table UC_USER_EXT
  is '外网用户信息扩展表';
-- Add comments to the columns 
comment on column UC_USER_EXT.ID
  is '关联用户表UC_USER的id';
comment on column UC_USER_EXT.SEX
  is '性别';
comment on column UC_USER_EXT.NATIVEPLACE
  is '籍贯';
comment on column UC_USER_EXT.NATION
  is '民族';
comment on column UC_USER_EXT.BIRTHDAY
  is '出生日期';
comment on column UC_USER_EXT.EDUCATION
  is '学历';
comment on column UC_USER_EXT.POLITICALSTATUS
  is '政治面貌';
comment on column UC_USER_EXT.HOMEADDRESS
  is '户口所在地';
comment on column UC_USER_EXT.POSTCODE
  is '邮政编码';
----------------------------End--------
  
------------2016-2-24------Start--dfk-----
  -- Add comments to the columns 
comment on column PUB_SMS.CHANNEL
  is '来自栏目（register：注册、notice：通知或告知、complain：投诉、consult：咨询，appointment：预约、others：其他）';
----------------------------End--------

------------2016-2-29------Start--lk-
create table PUB_COMMENT
(
  id           VARCHAR2(50) not null,
  cid          VARCHAR2(50),
  content      CLOB,
  ctime        DATE,
  creator      VARCHAR2(30),
  status       CHAR(1) default 0,
  creator_name VARCHAR2(10),
  region_code  VARCHAR2(50)
)
-- Add comments to the columns 
comment on column PUB_COMMENT.id
  is '主键';
comment on column PUB_COMMENT.cid
  is '内容发布ID';
comment on column PUB_COMMENT.content
  is '评论内容';
comment on column PUB_COMMENT.ctime
  is '评论时间';
comment on column PUB_COMMENT.creator
  is '评论人ID';
comment on column PUB_COMMENT.status
  is '评论状态 默认0 通过 1不通过';
comment on column PUB_COMMENT.creator_name
  is '评论人';
comment on column PUB_COMMENT.region_code
  is '行政区划';
----------------------------End--------

------------2016-3-5------Start--lk-
alter table STAR_LEVEL_EVALUATION add IS_EVALUATE CHAR(1);
comment on column STAR_LEVEL_EVALUATION.IS_EVALUATE
  is '是否已统计 0否1是';
----------------------------End--------

------------2016-3-6------Start--dfk-
alter table STAR_LEVEL_EVALUATION add CONVENIENCE_STAR_LEVEL NUMBER;
comment on column STAR_LEVEL_EVALUATION.CONVENIENCE_STAR_LEVEL
  is '便捷性评价';
----------------------------End--------

------------2016-3-17------Start--dfk---
 alter table UC_USER_EXT add ORGCODEVALIDPERIODSTART_STR VARCHAR2(64);
 alter table UC_USER_EXT add ORGCODEVALIDPERIODEND_STR   VARCHAR2(64);
 alter table UC_USER_EXT add ORGENGLISHNAME              VARCHAR2(200);
 alter table UC_USER_EXT add ORGTYPE                     VARCHAR2(64);
 alter table UC_USER_EXT add ORGACTUALITY                VARCHAR2(64);
 alter table UC_USER_EXT add ENTERPRISESORTCODE          VARCHAR2(200);
 alter table UC_USER_EXT add ENTERPRISESORTNAME          VARCHAR2(200);
 alter table UC_USER_EXT add ORGCODEAWARDDATE_STR        VARCHAR2(64);
 alter table UC_USER_EXT add REGISTERDATE_STR            VARCHAR2(64);
 alter table UC_USER_EXT add ORGCODEAWARDORG 			 VARCHAR2(200);
comment on column UC_USER_EXT.ORGCODEAWARDORG
  is '组织机构代码发证机构';
comment on column UC_USER_EXT.ORGCODEVALIDPERIODSTART_STR
  is '组织机构代码证有效期起';
comment on column UC_USER_EXT.ORGCODEVALIDPERIODEND_STR
  is '组织机构代码证有效期止';
comment on column UC_USER_EXT.ORGENGLISHNAME
  is '机构英文名称';
comment on column UC_USER_EXT.ORGTYPE
  is '组织机构类型Enterprise：企业、GovDept：政府机关、Institutions：事业单位、SocialOrg：社会团体';
comment on column UC_USER_EXT.ORGACTUALITY
  is '组织机构现状Change：变更、Deregister：注销、Register：设立、Revoke：吊销';
comment on column UC_USER_EXT.ENTERPRISESORTCODE
  is '企业类别代码';
comment on column UC_USER_EXT.ENTERPRISESORTNAME
  is '企业类别名称';
comment on column UC_USER_EXT.ORGCODEAWARDDATE_STR
  is '组织机构代码证发证日期';
comment on column UC_USER_EXT.REGISTERDATE_STR
  is '登记注册日期';

----------------------------End--------
------------2016-3-22------Start--lhy---
-- Create table
create table UC_LICENSE
(
  ID                VARCHAR2(100),
  CARD_ID           VARCHAR2(100),
  DOCID             VARCHAR2(20),
  TYPE              VARCHAR2(2) default 1,
  LICENSENAME       VARCHAR2(100),
  LICENSECODE       VARCHAR2(100),
  REMARK            VARCHAR2(100),
  FILENAME          VARCHAR2(100),
  CTIME             DATE,
  STATE             VARCHAR2(10),
  LICENSEFILENUMBER VARCHAR2(100),
  LICENSETYPE       VARCHAR2(100),
  REASON            VARCHAR2(1000),
  ORG_NAME          VARCHAR2(100),
  ORG_CODE          VARCHAR2(100)
)
tablespace ICITY
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64
    next 8
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table 
comment on table UC_LICENSE
  is '与用户信息绑定的证照信息';
-- Add comments to the columns 
comment on column UC_LICENSE.ID
  is 'ID';
comment on column UC_LICENSE.CARD_ID
  is '用户证件号码 例 ：个人为身份证 企业为组织机构代码';
comment on column UC_LICENSE.DOCID
  is '网盘返回的文档地址';
comment on column UC_LICENSE.TYPE
  is '默认为1，和正常上传的资料一样，从电子证照系统获取的信息提交到审批时type为3';
comment on column UC_LICENSE.LICENSENAME
  is '证照名称';
comment on column UC_LICENSE.LICENSECODE
  is '证照编码';
comment on column UC_LICENSE.REMARK
  is '其他';
comment on column UC_LICENSE.FILENAME
  is '上传的文件名称';
comment on column UC_LICENSE.CTIME
  is '时间';
comment on column UC_LICENSE.STATE
  is '0提交1审核通过2审核不通过3挂起';
comment on column UC_LICENSE.LICENSEFILENUMBER
  is '是电子证照或电子批文的文件编号——必填';
comment on column UC_LICENSE.LICENSETYPE
  is '是证照类型（证照：请用证照类型编码，如代码证则使用029010000；批文：请用“批文”）——必填';
comment on column UC_LICENSE.REASON
  is '审核意见或不通过通过原因';

----------------------------End--------

------------2016-3-24--待办视图todolist--------------------------lhy---------------------------
create or replace view todolist as
select 'ZX' type,
       '网上咨询' typename,
       t.ID,
       t.title,
       t.username name,
       t.write_date time,
       t.content content,
       t.depart_name deptname,
       t.depart_id deptid,
       t.region_id region_id,
       status,
       phone,
       deal_result recontent
  from guestbook t
 where type = '2' and t.write_date  is not null
union all
select 'TS' type,
       '网上投诉' typename,
       t.ID,
       t.title,
       t.username name,
       t.write_date time,
       t.content content,
       t.depart_name deptname,
       t.depart_id deptid,
       t.region_id region_id,
       status,
              phone,
              deal_result recontent
  from guestbook t
 where type = '3' and t.write_date  is not null
 union all
select 'YY' type,
       '非上班时间预约' typename,
       t.ID,
       t.YYRNAME title,
       t.YYRNAME name,
       to_date(t.yyday,'yyyy-MM-dd') time,
       t.yycontent content,
       t.yydeptname deptname,
       t.code deptid,
       t.regioncode region_id,
       status,
              yyrphone phone,
              re_content recontent
  from fsbsjyy t
union all
select 'YJ' type,
       '意见建议' typename,
       t.ID,
       t.title,
       t.username name,
       t.write_date time,
       t.content content,
       t.depart_name deptname,
       t.depart_id deptid,
       t.region_id region_id,
       status,
              phone,
              deal_result recontent
  from guestbook t
 where type = '10' and t.write_date  is not null;


--------------------------------------end----

------------2016-3-29------Start--lk------------
-- Create table
create table PUBLICITY_BASICINFO
(
  id          VARCHAR2(50),
  publicityid VARCHAR2(50),
  title       VARCHAR2(200),
  startdate   DATE,
  enddate     DATE,
  releasedate DATE,
  content     CLOB,
  region_id   VARCHAR2(50),
  dept_name   VARCHAR2(50),
  doc_id      VARCHAR2(50),
  file_name   VARCHAR2(200)
)
-- Add comments to the table 
comment on table PUBLICITY_BASICINFO
  is '信息公示表';
-- Add comments to the columns 
comment on column PUBLICITY_BASICINFO.id
  is '主键ID';
comment on column PUBLICITY_BASICINFO.publicityid
  is '公示ID';
comment on column PUBLICITY_BASICINFO.title
  is '公示标题';
comment on column PUBLICITY_BASICINFO.startdate
  is '公示开始日期';
comment on column PUBLICITY_BASICINFO.enddate
  is '公示结束日期';
comment on column PUBLICITY_BASICINFO.releasedate
  is '发布日期';
comment on column PUBLICITY_BASICINFO.content
  is '公示 详细内容';
comment on column PUBLICITY_BASICINFO.region_id
  is '区划ID';
comment on column PUBLICITY_BASICINFO.dept_name
  is '部门名称';
comment on column PUBLICITY_BASICINFO.doc_id
  is '附件ID';
comment on column PUBLICITY_BASICINFO.file_name
  is '附件名称';
-------------------end-------------------------------
 
 ------------2016-3-29------Start--kws---用于昆明市与省级对接区划映射-- 
 -- Create table
create table REGCODEMAPPING
(
  id     NUMBER not null,
  scode  CHAR(6),
  kmcode CHAR(12)
)
tablespace QZK
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table 
comment on table REGCODEMAPPING
  is '区划映射表';
-- Add comments to the columns 
comment on column REGCODEMAPPING.id
  is '主键ID';
comment on column REGCODEMAPPING.scode
  is '省级区划编码';
comment on column REGCODEMAPPING.kmcode
  is '昆明区划编码';
-- Create/Recreate primary, unique and foreign key constraints 
alter table REGCODEMAPPING
  add constraint SSS primary key (ID)
  using index 
  tablespace QZK
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );

  -- Add/modify columns
alter table GUESTBOOK add scode char(6) default 0;;

alter table PUB_CONTENT add scode char(6) default 0;


--------------------------------end--2016-03-29------------------------------------

------------2016-3-29------Start--lk-----------------
-- Create table
create table BANJIE_ATTACH
(
  id        VARCHAR2(50),
  sblsh     VARCHAR2(50),
  doc_id    VARCHAR2(50),
  file_name VARCHAR2(200)
)
-- Add comments to the table 
comment on table BANJIE_ATTACH
  is '办结附件表';
-- Add comments to the columns 
comment on column BANJIE_ATTACH.id
  is '主键ID';
comment on column BANJIE_ATTACH.sblsh
  is '申办流水号';
comment on column BANJIE_ATTACH.doc_id
  is '附件ID';
comment on column BANJIE_ATTACH.file_name
  is '附件名称';
--------------------end---------------------
-------------start---dfk-------2016-3-30---
-- Add/modify columns 
alter table UC_USER add is_phonebind VARCHAR2(1) default 0;
alter table UC_USER add is_emailbind VARCHAR2(1) default 0;
-- Add comments to the columns 
comment on column UC_USER.is_phonebind
  is '手机号是否绑定，1：绑定；0未绑定';
comment on column UC_USER.is_emailbind
  is '邮箱是否绑定，1：绑定；0未绑定';
  ----------------end-------------------
  -------------start----------2016-3-31---
delete from PUB_ID t where t.id not in('usercenter','mutual');
commit;
update PUB_ID t set t.prefix='' where t.id='usercenter';
commit;
----------------end-------------------
-------------start---lhy-------2016-4-13---
CREATE TABLE webfinal_audit_log (
  id varchar(36) NOT NULL,
  type varchar(1) DEFAULT NULL,
  app_code varchar(36) DEFAULT NULL,
  method varchar(255) DEFAULT NULL,
  params varchar(512) DEFAULT NULL,
  description varchar(512) DEFAULT NULL,
  request_ip varchar(128) DEFAULT NULL,
  exception_code varchar(30) DEFAULT NULL,
  exception_detail varchar(512) DEFAULT NULL,
  creator varchar(36) DEFAULT NULL,
  remark varchar(255) DEFAULT NULL,
  create_time date DEFAULT NULL,
  status varchar(1) DEFAULT NULL,
  PRIMARY KEY (id)
)
-- Add/modify columns 
alter table GUESTBOOK add ACCEPT VARCHAR2(10) default 0;
alter table GUESTBOOK add ACCEPTTIME DATE ;
alter table GUESTBOOK add ACCEPTTIMEOUT DATE ;
alter table GUESTBOOK add DEALTIMEOUT DATE ;
alter table GUESTBOOK add SATISFYCONTENT VARCHAR2(500) ;
alter table GUESTBOOK add SATISFYTIME DATE ;

-- Add comments to the columns 
 comment on column GUESTBOOK.ACCEPT
  is '是否受理，设0为未筛选1受理2为不受理。（淄博项目需求，先受理再回复）';
 comment on column GUESTBOOK.ACCEPTTIME
  is '受理或者不受理时间。目前主要用于比对是否筛选超期（淄博项目提出两天超期）';
 comment on column GUESTBOOK.ACCEPTTIMEOUT
  is '受理超期时间';
 comment on column GUESTBOOK.DEALTIMEOUT
  is '回复超期时间';
 comment on column GUESTBOOK.SATISFYCONTENT
  is 'satisfy评价内容';
 comment on column GUESTBOOK.SATISFYTIME
  is 'satisfy评价时间';
  ----------------end-------------------
  -------------start---lhy-------2016-4-18---
-- Add/modify columns 
alter table PUB_CONTENT add CREATORNAME VARCHAR2(100);
-- Add comments to the columns 
comment on column PUB_CONTENT.CREATORNAME
  is '创建人名称（根据需求新增当前用户姓名）';
----------------end-------------------
-------------start---lhy-------2016-4-23---
-- Add/modify columns 
alter table GUESTBOOK add INITLOG VARCHAR2(1) default 0;
-- Add comments to the columns 
 comment on column GUESTBOOK.INITLOG
  is '搜索引擎初始化标识0不初始化1初始化';
----------------end-------------------
-------------start---dfk-------2016-5-3---
-- Add comments to the columns 
comment on column GUESTBOOK.TYPE
  is '信件类别，分为咨询-2、投诉-3、意见建议-4、投资项目咨询-10、纠错-11、求助-12、其他';
-- Add/modify columns 
alter table GUESTBOOK modify BUSI_ID VARCHAR2(48);
----------------end-------------------
-------------start---lhy-------2016-5-5---
-- Add comments to the columns 
 comment on column FSBSJYY.STATUS
  is '回复状态0未处理1受理2不受理';
----------------end-------------------
-------------start---dfk-------2016-5-9---
-- Add comments to the columns 
comment on column GUESTBOOK.TYPE
  is '信件类别，分为咨询-2、投诉-3、意见建议-10、投资项目咨询-4、纠错-11、求助-12、其他';
----------------end------------------

-----------------------start---lhy-------2016-5-23---------------------------------------
  -- Add/modify columns 
alter table BUSINESS_INDEX add LICENSE CLOB;
-- Add comments to the columns 
comment on column BUSINESS_INDEX.LICENSE
  is '证照信息';
  ---------------------end------------------------------------------------
--------------start----------2016-5-25---------------
  -- Add/modify columns 
alter table GUESTBOOK add COMPLAIN_TYPE VARCHAR2(32);
-- Add comments to the columns 
comment on column GUESTBOOK.COMPLAIN_TYPE
  is '投诉类型：1、办事效率2、服务态度 （多个值用逗号分开）';
  -- Add comments to the columns 
comment on column GUESTBOOK.OPEN
  is '可公开讨论(0否1是)；投诉是否公开标识（0否1是）';
---------------------end-----------------------
  --------------start----------2016-5-26---------------
  -- Add/modify columns 
  alter table UC_USER add CREDIT_CODE VARCHAR2(50);
  -- Add comments to the columns 
  comment on column UC_USER.CREDIT_CODE
  is '统一信用代码';
---------------------end-----------------------

  --------------start----------2016-5-26---------------
  -- Add/modify columns 
  alter table GUESTBOOK add REPLYTORECORD CLOB;
  -- Add comments to the columns 
  comment on column GUESTBOOK.REPLYTORECORD
  is '回复记录';
---------------------end-----------------------
--------------start----------2016-5-27------------
-- Add comments to the columns 
comment on column UC_USER.STATUS
  is '用户状态(1:普通注册，2:实名认证，3:高级认证)';
  -- Drop columns 
alter table UC_USER drop column CARD_OFF_STATUS;
alter table UC_USER drop column MOBILE_OFF_STATUS;
  --------------------end---------------------
--------------start----------2016-5-26-------------
create or replace view todolist as
select 'ZX' type,
       '网上咨询' typename,
       t.ID,
       '（咨询）'||t.title title,
       t.username name,
       t.write_date time,
       t.content content,
       t.depart_name deptname,
       t.depart_id deptid,
       t.region_id region_id,
       status,
       phone,
       deal_result recontent
  from guestbook t
 where type = '2' 
 and status in ('0','4'.'5') 
 and t.write_date  is not null
union all
select 'TS' type,
       '网上投诉' typename,
       t.ID,
       '（投诉）'||t.title title,
       t.username name,
       t.write_date time,
       t.content content,
       t.depart_name deptname,
       t.depart_id deptid,
       t.region_id region_id,
       status,
              phone,
              deal_result recontent
  from guestbook t
 where type = '3' 
 and status in ('0','4','5') 
 and t.write_date  is not null
 union all
select 'YY' type,
       '非上班时间预约' typename,
       t.ID,
       '（预约）'||t.YYRNAME title,
       t.YYRNAME name,
       to_date(t.yyday,'yyyy-MM-dd') time,
       t.yycontent content,
       t.yydeptname deptname,
       t.code deptid,
       t.regioncode region_id,
       status,
              yyrphone phone,
              re_content recontent
  from fsbsjyy t
union all
select 'YJ' type,
       '意见建议' typename,
       t.ID,
       '（建议）'||t.title title,
       t.username name,
       t.write_date time,
       t.content content,
       t.depart_name deptname,
       t.depart_id deptid,
       t.region_id region_id,
       status,
              phone,
              deal_result recontent
  from guestbook t
 where type = '10' 
 and status in ('0','4','5') 
 and t.write_date  is not null;
--------------------------end--------------------------

  --------------start----------2016-5-26---------------
  -- Add/modify columns 
  alter table UC_USER add CREDIT_CODE VARCHAR2(50);
  -- Add comments to the columns 
  comment on column UC_USER.CREDIT_CODE
  is '统一信用代码';
---------------------end-----------------------

  --------------start----------2016-5-26---------------
  -- Add/modify columns 
  alter table GUESTBOOK add REPLYTORECORD CLOB;
  -- Add comments to the columns 
  comment on column GUESTBOOK.REPLYTORECORD
  is '回复记录';
---------------------end-----------------------
--------------start----------2016-5-27------------
-- Add comments to the columns 
comment on column UC_USER.STATUS
  is '用户状态(1:普通注册，2:实名认证，3:高级认证)';
  -- Drop columns 
alter table UC_USER drop column CARD_OFF_STATUS;
alter table UC_USER drop column MOBILE_OFF_STATUS;
  --------------------end---------------------
  --------------start----------2016-5-28---------------
drop function fun_business_serialnum;
drop function split_str;
drop function splittotable;
drop function fun_format_serialnum;
  --------------------end---------------------
  --------------start-----lhy-----2016-5-28---------------
-- Create table
create table PUB_STRATEGY
(
  ID           VARCHAR2(50) not null,
  SBLSH        VARCHAR2(50),
  SXID         VARCHAR2(50),
  SXBM         VARCHAR2(50),
  SXMC         VARCHAR2(50),
  CONTENT      CLOB,
  STATUS       VARCHAR2(10),
  SUBMITDATE   DATE,
  AUDITDATE    DATE,
  AUDITCONTENT CLOB,
  UCID         VARCHAR2(50),
  NAME         VARCHAR2(50),
  REVIEWER     VARCHAR2(50),
  REVIEWERID   VARCHAR2(50)
)
-- Add comments to the table 
comment on table PUB_STRATEGY
  is '我的攻略';
-- Add comments to the columns 
comment on column PUB_STRATEGY.ID
  is '攻略id';
comment on column PUB_STRATEGY.SBLSH
  is '关联业务的申办流水号';
comment on column PUB_STRATEGY.SXID
  is '事项id';
comment on column PUB_STRATEGY.SXBM
  is '事项编码';
comment on column PUB_STRATEGY.SXMC
  is '事项名称';
comment on column PUB_STRATEGY.CONTENT
  is '攻略内容';
comment on column PUB_STRATEGY.STATUS
  is '状态：0新增,1审核后发布,2审核后不发布';
comment on column PUB_STRATEGY.SUBMITDATE
  is '提交时间';
comment on column PUB_STRATEGY.AUDITDATE
  is '审核时间';
comment on column PUB_STRATEGY.AUDITCONTENT
  is '审核意见';
comment on column PUB_STRATEGY.UCID
  is '提交攻略的用户uid';
comment on column PUB_STRATEGY.NAME
  is '提交攻略的用户名称';
comment on column PUB_STRATEGY.REVIEWER
  is '审核人';
comment on column PUB_STRATEGY.REVIEWERID
  is '审核人id';

  --------------------end---------------------
    --------------start----------2016-5-29---------------
  -- Add/modify columns 
  alter table UC_USER add ICREGNUMBER VARCHAR2(50);
  -- Add comments to the columns 
  comment on column UC_USER.ICREGNUMBER
  is '工商注册号(营业执照)';
---------------------end-----------------------
---------start----------2016-06-06-------
-- Add/modify columns 
alter table UC_USER modify NAME null;
			
alter table BUSINESS_INDEX add LY VARCHAR2(20);
-- Add comments to the columns 
comment on column BUSINESS_INDEX.LY
is '来源，网上办事大厅：icity，微信：wechat，等';
-----------------end--------------------
---------start----------2016-06-15---lhy----
-- Add/modify columns 
alter table PUB_CHANNEL modify ID VARCHAR2(50);
alter table PUB_CONTENT modify ID VARCHAR2(50);
alter table PUB_CONTENT modify CID VARCHAR2(50);
alter table PUB_CHANNEL modify PARENT VARCHAR2(50);
-----------------end--------------------
---------start----------2016-06-15-------
-- Add comments to the columns 
comment on column STAR_LEVEL_EVALUATION.EVALUATE_TYPE
  is '评价类型 1办件评价 2部门评价3办事指南评价';
  -----------------end-------------------
  ---------start----------2016-06-17---lhy----
-- Add/modify columns 
alter table GUESTBOOK modify TITLE VARCHAR2(200 CHAR);
alter table GUESTBOOK modify ADDRESS VARCHAR2(200 CHAR);

alter table WEBFINAL_AUDIT_LOGmodify USER_ACCOUNT  VARCHAR2(128);
alter table WEBFINAL_AUDIT_LOGmodify USER_NAME VARCHAR2(255);
 -----------------end-------------------
 ---------start----------2016-06-17---dfk--- 
  -- Add/modify columns 
alter table GUESTBOOK add dispenseTIMEOUT date;
-- Add comments to the columns 
comment on column GUESTBOOK.dispenseTIMEOUT
  is '分发超期时间（若为空即不需要分发）';
  -- Add/modify columns 
alter table GUESTBOOK add ACCEPTTIMEWARN date;
alter table GUESTBOOK add DEALTIMEWARN date;
-- Add comments to the columns 
comment on column GUESTBOOK.ACCEPTTIMEWARN
  is '受理预警时间';
comment on column GUESTBOOK.DEALTIMEWARN
  is '办理预警时间';
  -----------------end------------------
     ---------start----------2016-06-22---dfk---
  -- Add comments to the columns 
comment on column PUB_SMS.CHANNEL
  is '来自栏目（register：注册、notice：通知或告知、 complain：投诉、consult：咨询，appointment：预约、query：查询业务、others：其他）';
    -----------------end------------------
    ---------start----------2016-06-22---dfk---
    -- Add/modify columns 
alter table BUSINESS_INDEX add pushstate VARCHAR2(1);
-- Add comments to the columns 
comment on column BUSINESS_INDEX.pushstate
  is '新消息推送状态：1  需要推送新消息；2  已推送；3  需要更新已推送消息状态';
  -- Add/modify columns 
alter table PUB_MYNEWS add writetime date;
-- Add comments to the columns 
comment on column PUB_MYNEWS.writetime
  is '消息写入时间';
  -- Add/modify columns 
alter table GUESTBOOK add PUSHSTATE CHAR(1);
-- Add comments to the columns 
comment on column GUESTBOOK.PUSHSTATE
  is '新消息推送状态：1  需要推送新消息；2  已推送；3  需要更新已推送消息状态';
  -- Add/modify columns 
alter table PUB_MYNEWS add newsid VARCHAR2(50);
-- Add comments to the columns 
comment on column PUB_MYNEWS.newsid
  is '待办消息的id（如申办流水号等）';
  -- Add/modify columns 
alter table PUB_MYNEWS add itemid VARCHAR2(50);
-- Add comments to the columns 
comment on column PUB_MYNEWS.itemid
  is '事项id';
      -----------------end------------------
  -- Add/modify columns 2016-6-30 by wq
alter table GUESTBOOK modify sxmc VARCHAR2(1000);
alter table STAR_LEVEL_EVALUATION modify apply_name VARCHAR2(1000);
alter table STAR_LEVEL_EVALUATION modify service_name VARCHAR2(1000);

---------start----------2016-07-4---lk---
create table BUSINESS_BILL_PAYMENT
(
  id         VARCHAR2(50),
  sblsh      VARCHAR2(50),
  billdetail CLOB,
  state      VARCHAR2(1),
  sxid       VARCHAR2(50)
)
tablespace ICITY
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 16
    next 8
    minextents 1
    maxextents unlimited
  );
-- Add comments to the columns 
comment on column BUSINESS_BILL_PAYMENT.id
  is '主键ID';
comment on column BUSINESS_BILL_PAYMENT.sblsh
  is '申办流水号';
comment on column BUSINESS_BILL_PAYMENT.billdetail
  is '缴费单据信息';
comment on column BUSINESS_BILL_PAYMENT.state
  is '缴费状态0未缴费 1成功 2失败';
comment on column BUSINESS_BILL_PAYMENT.sxid
  is '事项ID';
---------end----------2016-07-4---lk---
  
alter table GUESTBOOK add projectcode VARCHAR2(50);

---------start----------2016-07-05---dfk---
-- Create table
create table PUB_HALL
(
  ID           VARCHAR2(50) not null,
  NAME         VARCHAR2(50),
  ADDRESS      VARCHAR2(100),
  LONGITUDE    VARCHAR2(50),
  LATITUDE     VARCHAR2(50),
  TELPHONE     VARCHAR2(20),
  DES          VARCHAR2(200),
  CATAGORY     VARCHAR2(20),
  WORK_TIME    VARCHAR2(200),
  CREATER_NAME VARCHAR2(50),
  CREATER_ID   VARCHAR2(50),
  DEPT         VARCHAR2(50),
  REGION       VARCHAR2(50),
  STATE        VARCHAR2(2),
  CREATE_TIME  DATE,
  PUBLISH_TIME DATE
)
tablespace ICITY
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table 
comment on table PUB_HALL
  is '大厅网点';
-- Add comments to the columns 
comment on column PUB_HALL.ID
  is '主键id';
comment on column PUB_HALL.NAME
  is '网点名称';
comment on column PUB_HALL.ADDRESS
  is '网点地址';
comment on column PUB_HALL.LONGITUDE
  is '经度坐标';
comment on column PUB_HALL.LATITUDE
  is '纬度坐标';
comment on column PUB_HALL.TELPHONE
  is '联系电话';
comment on column PUB_HALL.DES
  is '网点描述';
comment on column PUB_HALL.CATAGORY
  is '网点分类';
comment on column PUB_HALL.WORK_TIME
  is '周一至周五 9：00-11：30；周六至周日13：30-17：00（存储格式：分八个时间：1、周一至周五上午开始时间；2、周一至周五上午结束时间；3、周一至周五下午开始时间；4、周一至周五下午结束时间；5、6、7、8、）';
comment on column PUB_HALL.CREATER_NAME
  is '创建人名字';
comment on column PUB_HALL.CREATER_ID
  is '创建人ID';
comment on column PUB_HALL.DEPT
  is '部门ID';
comment on column PUB_HALL.REGION
  is '区划ID';
comment on column PUB_HALL.STATE
  is '状态   00：暂存    01：发布 ';
comment on column PUB_HALL.CREATE_TIME
  is '创建时间';
comment on column PUB_HALL.PUBLISH_TIME
  is '发布时间';
  -- Add/modify columns 
alter table PUB_HALL add work_interval VARCHAR2(100);
-- Add comments to the columns 
comment on column PUB_HALL.WORK_TIME
  is '工作时间段（存储格式：分四个时间用分号分隔：1、上午开始时间；2、上午结束时间；3、下午开始时间；4、下午结束时间；）';
comment on column PUB_HALL.work_interval
  is '工作时间区间（周一至周五，或周一至周六）';
---------end--------------------------
---------start----------2016-07-07---dfk---
-- Add/modify columns 
alter table QUE_ANSWER_OF_QUESTION add CREATE_TIME date;
-- Add comments to the columns 
comment on column QUE_ANSWER_OF_QUESTION.CREATE_TIME
  is '记录生成时间';
  ---------end--------------------------
  
---------start----------2016-07-15---liukang---
-- Add/modify columns 
alter table BUSINESS_BILL_PAYMENT add PAYTIME date;
-- Add comments to the columns 
comment on column BUSINESS_BILL_PAYMENT.PAYTIME
  is '缴费时间';
alter table BUSINESS_BILL_PAYMENT add SUSPENDID date;
-- Add comments to the columns 
comment on column BUSINESS_BILL_PAYMENT.SUSPENDID
  is '挂起ID';
comment on column BUSINESS_BILL_PAYMENT.state
  is '缴费状态0未缴费 1外网缴费成功 2外网缴费失败 3窗口已缴费';
  ---------end--------------------------
---------start----------2016-07-18---dfk---
  -- Add/modify columns 
alter table STAR_LEVEL_EVALUATION add cryptonym varchar2(2);
-- Add comments to the columns 
comment on column STAR_LEVEL_EVALUATION.cryptonym
  is '是否匿名评价（1：是；0：否）';
    ---------end--------------------------
	
---------start----------2016-07-30---lhy---	
	-- Create table
create table BUSINESS_POWER_ATTACH
(
  ID         VARCHAR2(50),
  ATTACHID   VARCHAR2(50),
  YNAME      VARCHAR2(500),
  NAME       VARCHAR2(500),
  DOCID      VARCHAR2(50),
  UCID       VARCHAR2(50),
  UPLOADTIME VARCHAR2(50),
  ITEMBM     VARCHAR2(50),
  ITEMNAME   VARCHAR2(500)
)
tablespace ICITY
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64
    next 1
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table 
comment on table BUSINESS_POWER_ATTACH
  is '事项资料对应表';
-- Add comments to the columns 
comment on column BUSINESS_POWER_ATTACH.ID
  is 'ID';
comment on column BUSINESS_POWER_ATTACH.ATTACHID
  is '资料id';
comment on column BUSINESS_POWER_ATTACH.YNAME
  is '资料原名';
comment on column BUSINESS_POWER_ATTACH.NAME
  is '资料名';
comment on column BUSINESS_POWER_ATTACH.DOCID
  is '网盘文件ID';
comment on column BUSINESS_POWER_ATTACH.UCID
  is '用户ID';
comment on column BUSINESS_POWER_ATTACH.UPLOADTIME
  is '上传日期';
comment on column BUSINESS_POWER_ATTACH.ITEMBM
  is '事项编码';
comment on column BUSINESS_POWER_ATTACH.ITEMNAME
  is '事项名称';
    ---------end--------------------------
---------start----------2016-07-30---lhy---		
	-- Create table
create table DT_INDEXCATALOG
(
  ID            VARCHAR2(64) not null,
  COUNT         CLOB not null,
  VALIDITY_FLAG NUMBER(1) not null,
  EXCHANGE_TIME TIMESTAMP(6) not null,
  EX_FLAG       VARCHAR2(1) default 0 not null
)
tablespace ICITY
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64
    next 1
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table 
comment on table DT_INDEXCATALOG
  is '索引目录表结构';
-- Add comments to the columns 
comment on column DT_INDEXCATALOG.COUNT
  is 'XML';
comment on column DT_INDEXCATALOG.VALIDITY_FLAG
  is '数据有效标识：0无效；1有效。';
comment on column DT_INDEXCATALOG.EXCHANGE_TIME
  is '数据到市前置机的系统时间，格式为：yyyy-mm-dd hh24:mi:ss，由系统自动生成。';
comment on column DT_INDEXCATALOG.EX_FLAG
  is '0：未交换；1：已交换';
-- Create/Recreate primary, unique and foreign key constraints 
alter table DT_INDEXCATALOG
  add constraint PK_DT_INDEXCATALOG primary key (ID)
  using index 
  tablespace ICITY
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
    ---------end--------------------------
    ---------start----------2016-07-30---dfk---
    -- Add/modify columns 
alter table PUB_MYNEWS add EX_FLAG VARCHAR2(1) default 0;
-- Add comments to the columns 
comment on column PUB_MYNEWS.EX_FLAG
  is '0：未交换；1：已交换';
  -------------end--------------------------
      ---------start----------2016-08-05---wdl---
    -- Add/modify columns 
alter table GUESTBOOK add EX_FLAG VARCHAR2(1) default 0;
-- Add comments to the columns 
comment on column GUESTBOOK.EX_FLAG
  is '0：未交换；1：交换成功';
  -------------end--------------------------
  ---------start----------2016-08-05---dfk---
  alter table GUESTBOOK add DOC_ID VARCHAR2(500);
-- Add comments to the columns 
comment on column GUESTBOOK.DOC_ID
  is '上传网盘图片的doc_id';
------------end--------------------------
---------start----------2016-08-12---liukang---
-- Add/modify columns 
alter table BUSINESS_BILL_PAYMENT add ORDERID date;
-- Add comments to the columns 
comment on column BUSINESS_BILL_PAYMENT.ORDERID
  is '商户订单号（唯一交易凭证）';
  ---------end--------------------------
  ---modify---YNAME varchar2(100)->varchar2(200)-2016-8-17--yanhao---
  alter table ATTACH modify YNAME varchar2(200);
 ------------end--------------------------
 -- Add comments to the columns  
alter table UC_USER_EXT add MAILADDRESS CLOB;
comment on column UC_USER_EXT.MAILADDRESS
  is '邮寄地址（包含{HomeMAILAddress,CompanyMAILAddress}）';
  
---------start----------2016-09-05---liukang---
-- Add/modify columns 
alter table BUSINESS_BILL_PAYMENT add SOURCE VARCHAR2(20);
-- Add comments to the columns 
comment on column BUSINESS_BILL_PAYMENT.SOURCE
  is 'PSBC 邮储CCB建行';
  ---------end--------------------------
  
  -- Add/modify columns 
alter table IPRO_INDEX add MSG VARCHAR2(10);
-- Add comments to the columns 
comment on column IPRO_INDEX.MSG
  is '意见';
  
  -- Add/modify columns 
alter table IPRO_INDEX add STATUS VARCHAR2(1000);
-- Add comments to the columns 
comment on column IPRO_INDEX.STATUS
  is '状态0暂存1已提交2赋码成功3赋码失败4驳回5作废';
  ---------end--------------------------
 
 create table PUB_DATAEXCHANGE_ERROR_LOG
(
  ID           VARCHAR2(50),
  ERROR_TYPE   VARCHAR2(50),
  ERROR_NAME   VARCHAR2(50),
  ERROR_SIGN   VARCHAR2(50),
  CONTENT      VARCHAR2(255),
  CREATE_TIME  DATE
)
comment on table PUB_DATAEXCHANGE_ERROR_LOG
  is '事项资料对应表';
-- Add comments to the columns 
comment on column PUB_DATAEXCHANGE_ERROR_LOG.ID
  is '主键ID';
comment on column PUB_DATAEXCHANGE_ERROR_LOG.ERROR_TYPE
  is '出错数据类型';
 comment on column PUB_DATAEXCHANGE_ERROR_LOG.ERROR_NAME
  is '出错数据名称';
 comment on column PUB_DATAEXCHANGE_ERROR_LOG.ERROR_SIGN 
  is '出错数据标识';
 comment on column PUB_DATAEXCHANGE_ERROR_LOG.CONTENT
  is '出错原因';
 comment on column PUB_DATAEXCHANGE_ERROR_LOG.CREATE_TIME
  is '创建原因';
  alter table PUB_DATAEXCHANGE_ERROR_LOG modify ERROR_NAME varchar2(100);
  alter table PUB_DATAEXCHANGE_ERROR_LOG modify CONTENT varchar2(1000);
  alter table PUB_DATAEXCHANGE_ERROR_LOG add SEND_COUNT CHAR(1);
  comment on column PUB_DATAEXCHANGE_ERROR_LOG.SEND_COUNT
  is '推送失败次数';
------------start------------------
  -- Add/modify columns 
alter table PARALLEL_BIZ_BASE add STATE VARCHAR2(10);
-- Add comments to the columns 
comment on column PARALLEL_BIZ_BASE.STATE
  is '办件状态，0暂存；1提交；';
-------------end-------------------
------------start-----dfk----09/27---------
-- Add/modify columns 
alter table SUB_FOR_EX_APP_INFORMATION add ex_dataid VARCHAR2(50);
-- Add comments to the columns 
comment on column SUB_FOR_EX_APP_INFORMATION.ex_dataid
  is '外部表单dataid';

  -- Add/modify columns 
alter table UC_USER_EXT add BUSINESS_SCOPE VARCHAR2(1000);
-- Add comments to the columns 
comment on column UC_USER_EXT.BUSINESS_SCOPE
  is '经营（生产）范围（主营）';
    -------------end-------------------
 
  ---------start----------2016-10-12---dxl---
  -- Add/modify columns 
  alter table PUB_MSG_CONFIG add ITEM_NAME VARCHAR2(200);
-- Add comments to the columns 
  comment on column PUB_MSG_CONFIG.ITEM_NAME
  is '事项名称';
      -------------end-------------------
  
    ---------start----------2016-10-15---dxl---
  -- Add/modify columns 
  alter table ENTERPRISE_BUSINESS_INDEX add approve_num VARCHAR2(100);
  -- Add comments to the columns 
  comment on column ENTERPRISE_BUSINESS_INDEX.approve_num
  is '联办号';
-------------end-------------------
---------start----------2016-10-15---dfk--
  -- Add comments to the columns 
comment on column PUB_SMS.CHANNEL
  is '来自栏目（register：注册、notice：通知或告知、complain：投诉、consult：咨询，appointment：预约、query：查询业务、cg：投资项目抄告、resetpw：重置密码、others：其他）';
  ---------------end------------------
   ---yh---2016-10-20---Start--指南评价添加审核功能，增加状态字段-
alter table STAR_LEVEL_EVALUATION add STATE CHAR(1);
comment on column STAR_LEVEL_EVALUATION.STATE
  is '指南评价审核状态 0未审核  1审核通过  2审核未通过';
-----------------------------End--------
    ---yh---2016-10-20---Start--评价人姓名-
alter table STAR_LEVEL_EVALUATION add PJR_NAME VARCHAR2(50);
comment on column STAR_LEVEL_EVALUATION.PJR_NAME
  is '指南评价 评价人姓名';
-----------------------------End--------------
--------------------------删除 REGION_ID----------
 alter table STAR_LEVEL_EVALUATION drop column REGION_ID;
 -----------------------------End------------
---------start----------2016-10-31---lhy---
-- Create table
create table PARALLEL_CONSULTMSG
(
  ID             VARCHAR2(50),
  CONSULTFLOW    VARCHAR2(50),
  CONSULTSUBJECT VARCHAR2(100),
  FORMINFOJSON   CLOB,
  ITEMINFOJSON   CLOB,
  SUBMITDATE     DATE,
  UPDATEDATE     DATE,
  REMARK         VARCHAR2(100),
  STATUS         VARCHAR2(10),
  UCID           VARCHAR2(50)
)
tablespace ICITY
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64
    next 1
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table 
comment on table PARALLEL_CONSULTMSG
  is '并联前期咨询信息表';
-- Add comments to the columns 
comment on column PARALLEL_CONSULTMSG.ID
  is 'consultId业务咨询ID，咨询业务受理的唯一标示';
comment on column PARALLEL_CONSULTMSG.CONSULTFLOW
  is '咨询的流程ID';
comment on column PARALLEL_CONSULTMSG.CONSULTSUBJECT
  is '咨询主题';
comment on column PARALLEL_CONSULTMSG.FORMINFOJSON
  is '提交的表单信息';
comment on column PARALLEL_CONSULTMSG.ITEMINFOJSON
  is '反馈或者请求返回的结果集';
comment on column PARALLEL_CONSULTMSG.SUBMITDATE
  is '提交时间';
comment on column PARALLEL_CONSULTMSG.UPDATEDATE
  is '更新时间';
comment on column PARALLEL_CONSULTMSG.REMARK
  is '备注';
comment on column PARALLEL_CONSULTMSG.STATUS
  is '状态 0暂存 1已提交 2已回复';
comment on column PARALLEL_CONSULTMSG.UCID
  is '用户id';
---------------------------------
-- Create table
create table IPRO_BUSINESS_PROCESS
(
  ID                 VARCHAR2(50),
  SBLSH              VARCHAR2(50),
  STATE              VARCHAR2(50),
  CONTENT            CLOB,
  OPINION            VARCHAR2(500),
  TIME               DATE,
  CORRECTEDTIMELIMIT VARCHAR2(50),
  PRETREATMENTTIME   DATE
)
tablespace ICITY
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64
    next 8
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table 
comment on table IPRO_BUSINESS_PROCESS
  is '投资项目在线审批-单体事项审批流程';
-- Add comments to the columns 
comment on column IPRO_BUSINESS_PROCESS.STATE
  is '申报状态0暂存11：待预审(已提交)；12:预审通过；13：预审撤销； 14：预审驳回；
16：预审通过（舟山个性化）；
21：补齐补正；23：挂起；24：收费挂起； 25：挂起待审核；
96：退件；97：作废；98：不予许可；99：准许许可。
';
comment on column IPRO_BUSINESS_PROCESS.TIME
  is '插入时间';
comment on column IPRO_BUSINESS_PROCESS.CORRECTEDTIMELIMIT
  is '补齐补正时限';
comment on column IPRO_BUSINESS_PROCESS.PRETREATMENTTIME
  is '审批处理时间';
-------------------
-- Create table
create table IPRO_BUSINESS_INDEX
(
  ID          VARCHAR2(100) not null,
  ITEMID      VARCHAR2(100),
  ITEMNAME    VARCHAR2(200),
  PROJECTNAME VARCHAR2(200),
  PROJECTID   VARCHAR2(100),
  UCID        VARCHAR2(10),
  USERNAME    VARCHAR2(100),
  STATE       VARCHAR2(10),
  CONTENT     CLOB,
  FORMID      VARCHAR2(50),
  DATAID      VARCHAR2(50),
  CTIME       DATE,
  STEP        VARCHAR2(1),
  RECEIVENUM  VARCHAR2(100)
)
tablespace ICITY
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64
    next 8
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table 
comment on table IPRO_BUSINESS_INDEX
  is '投资项目单体事项在线办理业务表';
-- Add comments to the columns 
comment on column IPRO_BUSINESS_INDEX.ID
  is 'id';
comment on column IPRO_BUSINESS_INDEX.ITEMID
  is '事项id';
comment on column IPRO_BUSINESS_INDEX.ITEMNAME
  is '事项名';
comment on column IPRO_BUSINESS_INDEX.PROJECTNAME
  is '项目名';
comment on column IPRO_BUSINESS_INDEX.PROJECTID
  is '项目id';
comment on column IPRO_BUSINESS_INDEX.UCID
  is '用户id';
comment on column IPRO_BUSINESS_INDEX.USERNAME
  is '用户名';
comment on column IPRO_BUSINESS_INDEX.STATE
  is '申报状态0暂存,01:已受理；02:不予受理11：待预审(已提交)；12:预审通过；13：预审撤销； 14：预审驳回；
16：预审通过（舟山个性化）；
21：补齐补正；23：挂起；24：收费挂起； 25：挂起待审核；
96：退件；97：作废；98：不予许可；99：准许许可。
';
comment on column IPRO_BUSINESS_INDEX.CONTENT
  is '申报数据';
comment on column IPRO_BUSINESS_INDEX.FORMID
  is '表单id';
comment on column IPRO_BUSINESS_INDEX.DATAID
  is 'dataid';
comment on column IPRO_BUSINESS_INDEX.CTIME
  is '时间';
comment on column IPRO_BUSINESS_INDEX.STEP
  is '1表单2材料3提交';
-- Create/Recreate primary, unique and foreign key constraints 
alter table IPRO_BUSINESS_INDEX
  add constraint PK_IPRO_BUSINESS_INDEX primary key (ID)
  using index 
  tablespace ICITY
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
-----------------
-- Create table
create table PARALLEL_BUSINESS_INDEX
(
  ID          VARCHAR2(50) not null,
  BIZ_ID      VARCHAR2(50) not null,
  SUBMIT_TIME DATE,
  UPDATE_TIME DATE,
  ITEM_ID     VARCHAR2(50),
  ITEM_NAME   VARCHAR2(500),
  REGION_CODE VARCHAR2(50),
  REGION_NAME VARCHAR2(12),
  PROJECTCODE VARCHAR2(50),
  PROJECTNAME VARCHAR2(200),
  CONTENT     CLOB,
  STATUS      VARCHAR2(10)
)
tablespace ICITY
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64
    next 1
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table 
comment on table PARALLEL_BUSINESS_INDEX
  is '并联单体事项申报';
-- Add comments to the columns 
comment on column PARALLEL_BUSINESS_INDEX.BIZ_ID
  is '业务主体标识编号';
comment on column PARALLEL_BUSINESS_INDEX.SUBMIT_TIME
  is '提交时间';
comment on column PARALLEL_BUSINESS_INDEX.UPDATE_TIME
  is '状态更新时间';
comment on column PARALLEL_BUSINESS_INDEX.ITEM_ID
  is '事项编码';
comment on column PARALLEL_BUSINESS_INDEX.ITEM_NAME
  is '节点事项名称';
comment on column PARALLEL_BUSINESS_INDEX.REGION_CODE
  is '区划代码';
comment on column PARALLEL_BUSINESS_INDEX.REGION_NAME
  is '区划名称';
comment on column PARALLEL_BUSINESS_INDEX.PROJECTCODE
  is '项目编码';
comment on column PARALLEL_BUSINESS_INDEX.PROJECTNAME
  is '项目名称';
comment on column PARALLEL_BUSINESS_INDEX.CONTENT
  is '提交数据';
comment on column PARALLEL_BUSINESS_INDEX.STATUS
  is '状态 -2提交失败 -1不流转 0暂存 1办结 2在办';
-------------------------
-- Create table
create table LICENSE_AGENTAUTHORITYINFO
(
  ID                VARCHAR2(50),
  LICENSENO         VARCHAR2(150),
  LICENSENAME       VARCHAR2(150),
  AUTHORIZERNAME    VARCHAR2(50),
  AUTHORIZERNO      VARCHAR2(50),
  APPROVEITEMCODE   VARCHAR2(50),
  APPROVEITEMNAME   VARCHAR2(150),
  AUTHORIZEENAME    VARCHAR2(50),
  AUTHORIZEENO      VARCHAR2(50),
  VALIDTIME         DATE,
  AGENTTIME         DATE,
  RETURNID          VARCHAR2(50),
  STATUS            VARCHAR2(10),
  AUTHORIZERUCID    VARCHAR2(10),
  AUTHORIZEEUCID    VARCHAR2(10),
  AUTHORIZEEACCOUNT VARCHAR2(50),
  IS_USE            VARCHAR2(1)
)
tablespace ICITY
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64
    next 8
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table 
comment on table LICENSE_AGENTAUTHORITYINFO
  is '证照授权信息表';
-- Add comments to the columns 
comment on column LICENSE_AGENTAUTHORITYINFO.ID
  is 'id';
comment on column LICENSE_AGENTAUTHORITYINFO.LICENSENO
  is '电子证照的证照编码';
comment on column LICENSE_AGENTAUTHORITYINFO.LICENSENAME
  is '被委托授权电子证照名称';
comment on column LICENSE_AGENTAUTHORITYINFO.AUTHORIZERNAME
  is '授权者名称';
comment on column LICENSE_AGENTAUTHORITYINFO.AUTHORIZERNO
  is '授权者证件号码';
comment on column LICENSE_AGENTAUTHORITYINFO.APPROVEITEMCODE
  is '办理事项编码';
comment on column LICENSE_AGENTAUTHORITYINFO.APPROVEITEMNAME
  is '办理事项名称';
comment on column LICENSE_AGENTAUTHORITYINFO.AUTHORIZEENAME
  is '被授权人姓名';
comment on column LICENSE_AGENTAUTHORITYINFO.AUTHORIZEENO
  is '被授权人证件号码';
comment on column LICENSE_AGENTAUTHORITYINFO.VALIDTIME
  is '授权有效期';
comment on column LICENSE_AGENTAUTHORITYINFO.AGENTTIME
  is '授权时间';
comment on column LICENSE_AGENTAUTHORITYINFO.RETURNID
  is '证照系统授权编码';
comment on column LICENSE_AGENTAUTHORITYINFO.STATUS
  is '0提交1授权成功2授权失败';
comment on column LICENSE_AGENTAUTHORITYINFO.AUTHORIZERUCID
  is '授权人用户id';
comment on column LICENSE_AGENTAUTHORITYINFO.AUTHORIZEEUCID
  is '被授权人用户ID';
comment on column LICENSE_AGENTAUTHORITYINFO.AUTHORIZEEACCOUNT
  is '被授权人登陆账号';
comment on column LICENSE_AGENTAUTHORITYINFO.IS_USE
  is '是否可用 1可用 0 不可用';
------------------------
  -- Add/modify columns 
  alter table BUSINESS_ATTACH add URL VARCHAR2(50);
  -- Add comments to the columns 
  comment on column BUSINESS_ATTACH.URL
  is '附件名称（在服务器上的名称）';
    -- Add comments to the columns 
  comment on column BUSINESS_ATTACH.NAME
  is '附件名称（网办里的名称）';
-------------end------------------------------
-------------start----2016-11-10---dfk-----------
-- Add/modify columns 
alter table BUSINESS_EXPRESS add istaken CHAR(1);
alter table BUSINESS_EXPRESS add order_number VARCHAR2(50);
-- Add comments to the columns 
comment on column BUSINESS_EXPRESS.EXPRESS_ID
  is '快递单（运单号）';
comment on column BUSINESS_EXPRESS.istaken
  is '调用上门取件接口是否成功，0：未成功；1：成功；';
comment on column BUSINESS_EXPRESS.order_number
  is '订单号';
  -- Add/modify columns 
alter table BUSINESS_EXPRESS add TAKESTATE CHAR(1);
-- Add comments to the columns 
comment on column BUSINESS_EXPRESS.TAKESTATE
  is '上门取件（揽投）返回状态';
  -- Add/modify columns 
alter table BUSINESS_EXPRESS add EXPRESSID_RETURN VARCHAR2(50);
-- Add comments to the columns 
comment on column BUSINESS_EXPRESS.EXPRESS_ID
  is '快递单（运单号，由申请人发起的订单）';
comment on column BUSINESS_EXPRESS.EXPRESSID_RETURN
  is '快递单（运单号，由办公人员发起的订单）';
  -------------end-----------------------------
  -------------star--------16.11.19----------
  -- Add/modify columns 
alter table PUB_CONTENT add Issued_No VARCHAR2(200);
alter table PUB_CONTENT add written_time date;
-- Add comments to the columns 
comment on column PUB_CONTENT.Issued_No
  is '公文字号';
comment on column PUB_CONTENT.written_time
  is '成文时间';
  -- Add/modify columns 
alter table GUESTBOOK modify DEAL_RESULT VARCHAR2(2000 CHAR);
  --------------end------------------------
  
  -- Add comments to the columns  yanhao---
comment on column PUB_SMS.CHANNEL
  is '来自栏目（register：注册、notice：通知或告知、complain：投诉、consult：咨询，appointment：预约、query：查询业务、cg：投资项目抄告、resetpwd：重置密码、onlineApply:网上申报、others：其他）';
  ---------------end------------------
  --新建追问回复表  yanhao--- 
  create table GUESTBOOK_AGAIN_QA
(
  ID           VARCHAR2(50),
  G_ID         VARCHAR2(50),
  QUESTION     VARCHAR2(600),
  ANSWER       VARCHAR2(2000),
  Q_NAME       VARCHAR2(50),
  Re_NAME      VARCHAR2(50),
  Q_TIME       DATE,
  Re_TIME      DATE
);
comment on table GUESTBOOK_AGAIN_QA
  is '追问数据列表';
-- Add comments to the columns 
comment on column GUESTBOOK_AGAIN_QA.ID
  is '主键ID';
comment on column GUESTBOOK_AGAIN_QA.G_ID 
  is '外键,关联GUESTBOOK表ID';
  comment on column GUESTBOOK_AGAIN_QA.QUESTION 
  is '追问内容';
  comment on column GUESTBOOK_AGAIN_QA.ANSWER 
  is '回复内容';
  comment on column GUESTBOOK_AGAIN_QA.Q_NAME 
  is '追问人';
  comment on column GUESTBOOK_AGAIN_QA.Re_NAME 
  is '回复人';
   comment on column GUESTBOOK_AGAIN_QA.Q_TIME 
  is '追问时间';
  comment on column GUESTBOOK_AGAIN_QA.Re_TIME 
  is '回复时间';
   --新建追问回复表 结束--- 
   
 --GUESTBOOK 字段 状态添加 追问未回复 追问回复 yanhao--- 
  comment on column GUESTBOOK.status
  is '状态，0-未办理、1-已回复、3-审核通过、4-审核不通过、5追问未回复，6追问已回复，默认为未办理';
  -------end----
  
    -- Add comments to the columns  duxiaolong---
alter table QUE_ANSWER add EMAIL VARCHAR2(50);
comment on column QUE_ANSWER.EMAIL is '邮箱';
 -------end-----------

-----附件 文件名长度 由 50 改为 100 yanhao----
alter table BUSINESS_ATTACH modify NAME VARCHAR2(100);
--------------------end--------

-- Add comments to the columns 
comment on column GUESTBOOK.TYPE
  is '信件类别，分为咨询-2、投诉-3、意见建议-4、投资项目咨询-10、纠错-11、求助-12、主任信箱-20（漳州）、其他';

  -- Add comments to the columns  盘龙使用 --yanhao
alter table PUB_SMS add EMAIL VARCHAR2(50);
comment on column PUB_SMS.EMAIL
  is '邮箱';
alter table PUB_SMS add noticeType VARCHAR2(50);
comment on column PUB_SMS.noticeType
  is '通知类型：sms:短息，email:邮件';
  
 --PUB_MSG_CONFIG 短信管理表 增加用户 邮件信息字段 
 alter table PUB_MSG_CONFIG add EMAIL VARCHAR2(50);
comment on column PUB_MSG_CONFIG.EMAIL
  is '邮箱';
   -- modify数据交换 ERROR_NAME 100->200 yanhao
 alter table PUB_DATAEXCHANGE_ERROR_LOG modify ERROR_NAME varchar2(200);