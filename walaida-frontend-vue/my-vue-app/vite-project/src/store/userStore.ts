import { defineStore } from "pinia";
import { ref } from "vue";
import { getLoginUserUsingGet } from "../api/userController";
import ACCESS_ENUM from "../access/accessEnum";

// 导出一个名为 useLoginUserStore 的常量，该常量通过 defineStore 函数定义了一个 Vuex store 模块
export const useLoginUserStore = defineStore("loginUser", () => {
  // 定义一个响应式变量 loginUser，初始值为一个包含 userName 属性的对象，初始值为 "未登录"
  const loginUser = ref<API.LoginUserVO>({
    userName: "未登录",
  });

  // 定义一个异步函数 fetchLoginUser，用于从服务器获取登录用户信息
  async function fetchLoginUser() {
    // 调用 getLoginUserUsingGet 函数获取登录用户信息，并等待响应
    const res = await getLoginUserUsingGet();
    // 检查响应数据中的 code 是否为 0（表示成功）且 data 是否存在
    if (res.data.code === 0 && res.data.data) {
      // 如果条件满足，将响应数据中的 data 赋值给 loginUser
      loginUser.value = res.data.data;
    } else {
      loginUser.value = {
        //用户权限未登录
        userRole: ACCESS_ENUM.NOT_LOGIN,
      };
    }
  }

  // 定义一个函数 setLoginUser，用于设置 loginUser 的值
  function setLoginUser(newLoginUser: API.LoginUserVO) {
    // 将传入的新登录用户信息赋值给 loginUser
    loginUser.value = newLoginUser;
  }

  // 返回一个对象，包含 loginUser、setLoginUser 和 fetchLoginUser 三个属性
  return { loginUser, setLoginUser, fetchLoginUser };
});
