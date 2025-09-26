// @ts-ignore
/* eslint-disable */
import request from "@/utils/request";

/** applyUserRecharge POST /api/userRecharge/apply */
export async function applyUserRechargeUsingPost(
  body: API.UserRechargeApplyRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong_>("/api/userRecharge/apply", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    data: body,
    ...(options || {})
  });
}

/** listMyUserRechargeVOByPage POST /api/userRecharge/my/list/page/vo */
export async function listMyUserRechargeVoByPageUsingPost(
  body: API.UserRechargeQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageUserRechargeRequestVO_>(
    "/api/userRecharge/my/list/page/vo",
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      data: body,
      ...(options || {})
    }
  );
}

/** listUserRechargeVOByPage POST /api/userRecharge/list/page/vo */
export async function listUserRechargeVoByPageUsingPost(
  body: API.UserRechargeQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageUserRechargeRequestVO_>(
    "/api/userRecharge/list/page/vo",
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      data: body,
      ...(options || {})
    }
  );
}

/** reviewUserRecharge POST /api/userRecharge/review */
export async function reviewUserRechargeUsingPost(
  body: API.UserRechargeReviewRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/userRecharge/review", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    data: body,
    ...(options || {})
  });
}
