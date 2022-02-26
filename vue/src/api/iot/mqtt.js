import axios from 'axios'

const username = 'admin';
const password = 'public';

// 集群下所有客户端列表
export function listMqttClient(query) {  
  var url = "/api/v4/clients?_page=1&_limit=10&clientid=server-db411ef8027f0d74fec5b702ccac313e";
  return axios({
    method: 'get',
    url: url,
    auth: {
      username: username,
      password: password
    },
    params: query
  })
}
