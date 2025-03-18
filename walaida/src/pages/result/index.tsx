import { View, Image } from "@tarojs/components";
import { AtButton } from "taro-ui";
import Taro from "@tarojs/taro";
import "taro-ui/dist/style/components/button.scss"; // 按需引入
import "./index.scss";
import imgs from "../../img/home.jpg";
import QuestionResult from "../data/qustionResult.json";
import Qustions from "../data/qustions.json";
import { getMaxScore } from "../utils/bizUtils";

export default () => {
  const questions = Qustions;
  const question_results = QuestionResult;
  const answerList = Taro.getStorageSync("valueList");
  if (!answerList || answerList.length < 1) {
    Taro.showToast({
      title: "答案为空",
      icon: "error",
      duration: 3000,
    });
  }

  const result = getMaxScore(answerList, questions, question_results);
  if (!result) {
    Taro.showToast({
      title: "结果为空",
      icon: "error",
      duration: 3000,
    });
  }
  return (
    <View>
      <View
        className="index"
        style={`background-image: url(${imgs});  background-size: cover;background-repeat: no-repeat;

    /* 设置背景图片固定在视口 */
    background-attachment: fixed;

    /* 设置背景图片居中显示 */
    background-position: center center;

    /* 设置背景颜色，以防图片加载失败 */
    background-color: #ffffff;

    /* 在底部留有100px的空白 */
    padding-bottom: 100px;`}
      >
        <View className="at-article__h1">你的角色为：{result.resultName}</View>

        <View className="at-article__content">
          <View className="at-article__h2">
            您的性格特点：{result.resultDesc}
          </View>
        </View>
        <AtButton
          type="primary"
          className="btn"
          onClick={() => {
            //relauch 清空历史跳转页面跳转主页
            Taro.reLaunch({
              url: "/pages/index/index",
            });
          }}
        >
          返回主页
        </AtButton>
      </View>
      <View className="at-article__info">BIMI智能测试&nbsp;作者：哇来答</View>
    </View>
  );
};
