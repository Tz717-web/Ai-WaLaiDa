import { View, Image } from "@tarojs/components";
import { AtButton } from "taro-ui";
import Taro from "@tarojs/taro";
import "taro-ui/dist/style/components/button.scss"; // 按需引入
import "./index.scss";
import imgs from "../../img/yy.jpg";

export default () => {
  return (
    <View className="index">
      <View className="at-article__h1">BIMI性格测试</View>

      <View className="at-article__content">
        <View className="at-article__h2">
          只需2分钟测试你属于BIMI的什么人格
        </View>
      </View>
      <AtButton
        type="primary"
        className="btn"
        onClick={() => {
          Taro.navigateTo({
            url: "/pages/DoText/index",
          });
        }}
      >
        开始测试
      </AtButton>
      <Image
        style="width: 430px;height: 500px;background: #fff; margin-top: 20px;"
        src={imgs}
      />

      <View className="at-article__info">BIMI智能测试&nbsp;作者：哇来答</View>
    </View>
  );
};
