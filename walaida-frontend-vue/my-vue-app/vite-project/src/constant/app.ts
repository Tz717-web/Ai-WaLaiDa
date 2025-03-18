//审核状态枚举
export const REVIEW_STATUS_ENUM = {
  //待审核
  PENDING: 0,
  //审核通过
  PASS: 1,
  //审核不通过
  REJECT: 2,
};
//审核状态枚举
export const REVIEW_STATUS_MAP: { [appType: number]: string } = {
  0: "待审核",
  1: "通过",
  2: "拒绝",
};
//应用评分类型枚举
export const APP_TYPE_ENUM = {
  //得分类
  SCORE: 0,
  //测评类
  TEST: 1,
};
//应用类型映射
export const APP_TYPE_MAP:{[appType: number]: string} = {
  0: "得分类",
  1: "测评类",
};
//应用评分类型枚举
export const APP_SCORING_STRATEGY_ENUM = {
  //得分类
  CUSTOM: 0,
  //测评类
  AI: 1,
};
//应用类型映射
export const APP_SCORING_STRATEGY_MAP: { [appType: number]: string } = {
  0: "自定义",
  1: "AI",
};
