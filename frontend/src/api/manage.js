import request from '@/utils/request'

const api = {
  user: '/user',
  role: '/role',
  service: '/service',
  permission: '/permission',
  permissionNoPager: '/permission/no-pager',
  orgTree: '/org/tree',
  sprint: '/sprint/sprint',
  sprintrels: '/sprint/sprint/rels',
  sprintlist: '/sprint/sprintlist',
  comparesprint: '/sprint/compare/sprint',
  applist: '/app/applist',
  app: '/app/app',
  branchlist: '/code/branchList',
  appbranch: '/sprint/appbranch',
  task: '/task/task'
}

export default api

export function getUserList (parameter) {
  return request({
    url: api.user,
    method: 'get',
    params: parameter
  })
}

export function getRoleList (parameter) {
  return request({
    url: api.role,
    method: 'get',
    params: parameter
  })
}

export function getServiceList (parameter) {
  return request({
    url: api.service,
    method: 'get',
    params: parameter
  })
}

export function getPermissions (parameter) {
  return request({
    url: api.permissionNoPager,
    method: 'get',
    params: parameter
  })
}

export function getOrgTree (parameter) {
  return request({
    url: api.orgTree,
    method: 'get',
    params: parameter
  })
}

// id == 0 add     post
// id != 0 update  put
export function saveService (parameter) {
  return request({
    url: api.service,
    method: parameter.id === 0 ? 'post' : 'put',
    data: parameter
  })
}

export function saveSub (sub) {
  return request({
    url: '/sub',
    method: sub.id === 0 ? 'post' : 'put',
    data: sub
  })
}

export function getOneSprint (parameter) {
  return request({
    url: api.sprint + '?sprintid=8',
    method: 'get'
  })
}

export function getSprintRels (sprintid, type) {
  let url = api.sprintrels + '?sprintid=' + sprintid
  if (type) {
    url = url + '&type=' + type
  }
  return request({
    url: url,
    method: 'get'
  })
}

export function getSprintList (parameter) {
  return request({
    url: api.sprintlist + '?sprintcode=' + parameter.sprintcode + '&size=' + parameter.pageSize + '&page=' + parameter.pageNo,
    method: 'get'
  })
}

export function saveSprint (sprint) {
  return request({
    url: api.sprint,
    method: sprint.id ? 'put' : 'post',
    data: sprint
  })
}

export function deleteSprint (sprintcode) {
  return request({
    url: api.sprint + '?ids=' + sprintcode,
    method: 'delete'
  })
}

export function getApplist (parameter) {
  return request({
    url: api.applist + '?appcode=' + parameter.appcode + '&size=' + parameter.pageSize + '&page=' + parameter.pageNo,
    method: 'get'
  })
}

export function saveApp (app) {
  return request({
    url: api.app,
    method: app.id ? 'put' : 'post',
    data: app
  })
}

export function deleteApp (id) {
  return request({
    url: api.app + '?ids=' + id,
    method: 'delete'
  })
}

export function getCompareSprint () {
  return request({
    url: api.comparesprint,
    method: 'get'
  })
}

export function getBranchlist (appid) {
  return request({
    url: api.branchlist + '?appId=' + appid,
    method: 'get'
  })
}

export function getAppBranch (relid, appcode) {
  return request({
    url: api.appbranch + '?relid=' + relid + '&appcode=' + appcode,
    method: 'get'
  })
}

export function saveAppBranch (parameter) {
  return request({
    url: api.appbranch,
    method: parameter.id === 0 ? 'post' : 'put',
    data: parameter
  })
}

export function excuteTask (id, relid, appcode) {
  let url = api.task + '?appcode=' + appcode + '&relid=' + relid
  if (id) {
    url = url + '&id=' + id
  }
  return request({
    url: url,
    method: 'get'
  })
}
