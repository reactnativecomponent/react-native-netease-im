import React, { Component } from 'react';
import { NativeModules, requireNativeComponent } from 'react-native';
import PropTypes from 'prop-types';
const { RNVideoChatManager } = NativeModules

export default class VideoView extends Component {
  static propTypes = {
    width:PropTypes.number,
    height:PropTypes.number,
    call:PropTypes.func,
    accept:PropTypes.func,
    hangup:PropTypes.func,
  };
  // 拨打电话
  call (callee) {
    console.log('VideoView call:' + callee);
    return RNVideoChatManager.call(callee);
  }
  // 接听/拒绝
  accept (type,callid,from) {
    console.log('VideoView accept:' + type);
    return RNVideoChatManager.accept(type,callid,from);
  }
  // 挂断
  hangup () {
    console.log('VideoView hangup');
    return RNVideoChatManager.hangup();
  }
  render() {
    return <RNVideoChatV {...this.props} />;
  }
}
var RNVideoChatV = requireNativeComponent('RNVideoChat', VideoView);
