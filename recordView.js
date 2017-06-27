/**
 * Created by dowin on 2017/6/27.
 */

import React, {Component, PropTypes} from 'react';
import {View, requireNativeComponent} from 'react-native';

export default class RCTRecordView extends Component {

    _onchange = (event) => {
        if (this.props.onChange && event.nativeEvent) {
            this.props.onChange(event.nativeEvent);
        }
    }

    render() {
        return (
            <RecordView
                {...this.props}
                onChange={this._onchange}
            />);
    }
}
RCTRecordView.propTypes = {
    ...View.propTypes,
    textArr: PropTypes.arrayOf(PropTypes.string),
    onChange: PropTypes.func,
};
const RecordView = requireNativeComponent('RecordView', RCTRecordView);