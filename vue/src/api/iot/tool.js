import request from '@/utils/request'

// 注册方法
export function register(data) {
  return request({
    url: '/iot/tool/register',
    headers: {
      isToken: false
    },
    method: 'post',
    data: data
  })
}
