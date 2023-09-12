import { NativeModules, Platform } from "react-native";
const { RNNeteaseIm, PinYin } = NativeModules;

class NimUtils {
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
   * 播放录音
   * @returns {*}
   */
  play(filepath: string) {
    return RNNeteaseIm.play(filepath);
  }

  /**
   * 播放本地资源音乐
   * name：iOS：文件名字，Android：文件路径
   * type：音乐类型，如：'mp3'
   * @returns {*}
   */
  playLocal(name: string, type: string) {
    if (Platform.OS === "ios") {
      return RNNeteaseIm.playLocal(name, type);
    }
    return RNNeteaseIm.playLocal("assets:///" + name + "." + type, type);
  }

  /**
   * 停止播放录音
   * @returns {*}
   */
  stopPlay() {
    return RNNeteaseIm.stopPlay();
  }

  sortPinYin(o: any, key: string) {
    return PinYin.sortPinYin(o, key);
  }

  /**
   * Android only
   * @returns {*}
   */
  fetchNetInfo() {
    return RNNeteaseIm.fetchNetInfo();
  }
}

export default new NimUtils();
