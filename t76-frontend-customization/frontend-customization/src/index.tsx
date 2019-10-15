import React from 'react';

import {ExternalAppComponentProps, ExternalApplication} from '@flowable/work';

import {detailCustomizations} from './customizations/details';
import './themes/flowable/index.scss';
import StarRatingComponent from "./form-components/StarRatingComponent";
import {StarWarsApp} from "./apps/starWarsApp";
import {StarWarsTwitterApp} from "./apps/starWarsTwitterApp";

function createApps(): ExternalApplication[] {
  return [
    {
      applicationId: 'star-wars',
      label: 'Star Wars API',
      icon: 'sitemap/solid',
      component: (props: ExternalAppComponentProps) => <StarWarsApp {...props} />,
      sub: [
        {
          applicationId: 'star-wars',
          label: 'Dashboard',
          icon: 'sitemap/solid',
          component: (props: ExternalAppComponentProps) => <StarWarsApp {...props}/>
        },
        {
          applicationId: 'star-wars-twitter',
          label: 'Twitter',
          icon: 'sitemap/solid',
          component: (props: ExternalAppComponentProps) => <StarWarsTwitterApp {...props}/>
        }
      ]
    }
  ];
}


const applications = createApps();
const formComponents = [
  ['starRatingComponent', StarRatingComponent]
];

export default { applications, detailCustomizations, formComponents };
