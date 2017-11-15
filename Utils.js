'use strict'
import { NativeModules,Platform, NetInfo } from 'react-native';

const { RNNeteaseIm,PinYin } = NativeModules;

class Utils {
    /**
     * 清除数据缓存
     */
    cleanCache() {
        return RNNeteaseIm.cleanCache();
    }

    /**
     * 获取缓存大小
     */
    getCacheSize() {
        return RNNeteaseIm.getCacheSize();
    }

    /**
     * 获取网络状态
     */
    networkIsConnected() {//0：无网络，1：有网络
        if (Platform.OS === 'ios') {
            if (RNNeteaseIm.getNetWorkStatus() === '1') {
                return true;
            }
            return false;
        } else {
            return NetInfo.isConnected.fetch().done();
        }
    }

    /** ******************************播放 ****************************************** **/

    /**
     * 播放录音
     * @returns {*}
     */
    play(filepath) {
        return RNNeteaseIm.play(filepath);
    }

    /**
     * 播放本地资源音乐
     * name：iOS：文件名字，Android：文件路径
     * type：音乐类型，如：'mp3'
     * @returns {*}
     */
    playLocacl(name, type) {
        if (Platform.OS === 'ios') {
            return RNNeteaseIm.playLocal(name, type);
        }
        return RNNeteaseIm.playLocal('assets:///' + name + '.' + type, type);
    }

    /**
     * 停止播放录音
     * @returns {*}
     */
    stopPlay() {
        return RNNeteaseIm.stopPlay();
    }

    sortPinYin(o, key) {
        return PinYin.sortPinYin(o, key);
    }


    /**
     * 仅限Android
     * @returns {*}
     */
    fetchNetInfo() {
        return RNNeteaseIm.fetchNetInfo();
    }

    /**
     * 仅限IOS
     * 设置webview UA
     * @returns {*}
     */
    setupWebViewUserAgent() {
        RNNeteaseIm.setupWebViewUserAgent();
    }

}

export default new Utils();
