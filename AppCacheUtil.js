'use strict'
import { NativeModules,Platform } from 'react-native';

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
    static getNetWorkStatus(){//0：无网络，1：有网络
        return RNNeteaseIm.getNetWorkStatus();
    }
}

