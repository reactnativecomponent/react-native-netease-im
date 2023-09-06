import { NativeModules, Platform } from "react-native";
const { RNNeteaseIm, PinYin } = NativeModules;

/**
 * 清除数据缓存
 */
function cleanCache() {
  return RNNeteaseIm.cleanCache();
}

/**
 * 获取缓存大小
 */
function getCacheSize() {
  return RNNeteaseIm.getCacheSize();
}

/**
 * 播放录音
 * @returns {*}
 */
function play(filepath: string) {
  return RNNeteaseIm.play(filepath);
}

/**
 * 播放本地资源音乐
 * name：iOS：文件名字，Android：文件路径
 * type：音乐类型，如：'mp3'
 * @returns {*}
 */
function playLocal(name: string, type: string) {
  if (Platform.OS === "ios") {
    return RNNeteaseIm.playLocal(name, type);
  }
  return RNNeteaseIm.playLocal("assets:///" + name + "." + type, type);
}

/**
 * 停止播放录音
 * @returns {*}
 */
function stopPlay() {
  return RNNeteaseIm.stopPlay();
}

function sortPinYin(o: any, key: string) {
  return PinYin.sortPinYin(o, key);
}

/**
 * Android only
 * @returns {*}
 */
function fetchNetInfo() {
  return RNNeteaseIm.fetchNetInfo();
}

export const NimUtils = {
  cleanCache,
  getCacheSize,
  play,
  playLocal,
  stopPlay,
  sortPinYin,
  fetchNetInfo,
};
