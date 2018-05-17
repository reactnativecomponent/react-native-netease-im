/**
 * Created by dowin on 2017/8/2.
 */
'use strict'
import { NativeModules,Platform } from 'react-native'
const { RNVideoChatManager } = NativeModules
class ViewManager {
    /**
     * 登陆
     * @param account
     * @param token
     * @returns {*} @see observeRecentContact observeOnlineStatus
     */
    login(contactId, token) {
        return RNVideoChatManager.login(contactId, token)
    }
    /**
     * 退出
     * @returns {*}
     */
    logout() {
        return RNVideoChatManager.logout()
    }

}
export default new ViewManager()
