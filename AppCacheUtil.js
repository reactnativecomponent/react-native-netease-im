'use strict'
import { NativeModules,Platform, NetInfo } from 'react-native';

const { RNNeteaseIm } = NativeModules;

export default class AppCacheUtil{
    /**
     * 清除数据缓存
     */
    static cleanCache(){
        return RNNeteaseIm.cleanCache();
    }

    /**
     * 获取缓存大小
     */
    static getCacheSize(){
        return RNNeteaseIm.getCacheSize();
    }

    /**
     * 获取网络状态
     */
    static networkIsConnected(){//0：无网络，1：有网络
        if(Platform.OS === 'ios'){
           if(RNNeteaseIm.getNetWorkStatus() === '1'){
               return true;
           }
           return false;
        }else{
            return NetInfo.isConnected.fetch().done();
        }
    }

}

