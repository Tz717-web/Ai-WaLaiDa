import { View, Text } from "@tarojs/components";
import { AtButton, AtBadge, AtRadio, AtProgress } from "taro-ui";
import { useEffect, useState } from "react";
import Taro from "@tarojs/taro";
import "taro-ui/dist/style/components/button.scss"; // 按需引入
import "./index.scss";
import qustions from "../data/qustions.json";

export default () => {
  // 返回一个包含视图和文本的组件
  const qustion = qustions[0];
  const [currentIndex, setCurrentIndex] = useState<number>(1);
  const [currentQustion, setCurrentQustion] = useState(qustions[0]);
  const qustionList = currentQustion.options.map((option) => {
    return { label: `${option.key}. ${option.value}`, value: option.key };
  });
  const [currentValue, setCurrentValue] = useState<string>("");
  const [valueList] = useState<string[]>([]);
  useEffect(() => {
    setCurrentQustion(qustions[currentIndex - 1]);
    setCurrentValue(valueList[currentIndex - 1]);
  }, [currentIndex]);

  return (
    // 使用View组件作为容器，并设置类名为当前页面的名称
    <View className="<%= pageName %>">
      <View className="at-article__h1">
        {`${currentIndex} .${qustion.title}`}
      </View>
      <AtProgress
        color="#6190E8"
        status="progress"
        isHidePercent
        strokeWidth={12}
        percent={(currentIndex / qustions.length) * 100}
      ></AtProgress>
      <AtRadio
        options={qustionList}
        value={currentValue}
        onClick={(value) => {
          setCurrentValue(value);
          valueList[currentIndex - 1] = value;
        }}
      />

      {currentIndex > 1 && (
        <AtButton
          className="btn1"
          onClick={() => setCurrentIndex(currentIndex - 1)}
        >
          上一题
        </AtButton>
      )}
      {currentIndex < qustions.length && (
        <AtButton
          type="primary"
          //选择不能为空
          disabled={!currentValue}
          className="btn"
          onClick={() => setCurrentIndex(currentIndex + 1)}
        >
          下一题
        </AtButton>
      )}
      {currentIndex === qustions.length && (
        <AtButton
          type="primary"
          className="btn"
          //选择不能为空
          disabled={!currentValue}
          onClick={() => {
            Taro.setStorageSync("valueList", valueList);
            Taro.navigateTo({
              url: "/pages/result/index",
            });
          }}
        >
          查看结果
        </AtButton>
      )}
    </View>
  );
};
