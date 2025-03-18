// @ts-ignore
/* eslint-disable */
import request from "../request";

/** getAppAnswerCount GET /api/app/statistic/answerCount */
export async function getAppAnswerCountUsingGet(options?: {
  [key: string]: any;
}) {
  return request<API.BaseResponseListAppAnswerCountDTO_>(
    "/api/app/statistic/answerCount",
    {
      method: "GET",
      ...(options || {}),
    }
  );
}

/** getAppAnswerResultCount GET /api/app/statistic/answerResultCount */
export async function getAppAnswerResultCountUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getAppAnswerResultCountUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseListAppAnswerResultCountDTO_>(
    "/api/app/statistic/answerResultCount",
    {
      method: "GET",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}
