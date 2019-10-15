import React from 'react';
import insertCss from 'insert-css'
import {_, Model} from '@flowable/forms';

import {css, StarRatingInput} from "react-star-rating-input";

type PrefillButtonProps = Model.Props & {};

type StarRatingComponentButtonState = {
  value: number;
};

class StarRatingComponent extends Model.FormComponent {
  state: StarRatingComponentButtonState;

  constructor(props: PrefillButtonProps) {
    super(props);
    this.state = {
      value: 0
    };
    insertCss(css);
  }

  componentDidMount(): void {
    const { value, defaultValue} = this.props.config;
    if(value){
      this.setState({selectedValue: value})
    } else {
      this.setState({selectedValue: defaultValue})
    }
  }

  handleChange(value: number): void {
    const {config, onChange, onEvent} = this.props;

    onChange(value);
  }

  render() {
    const { config } = this.props;
    const stars: number = _.get(config, 'extraSettings.stars');

    return <>
      Star rating:
      <StarRatingInput
          size={stars}
          value={config.value}
          onChange={(v) => this.handleChange(v)}/></>;
  }
}

export default StarRatingComponent;
