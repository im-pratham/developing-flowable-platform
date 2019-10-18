import React from 'react';

import {TwitterTimelineEmbed} from 'react-twitter-embed/dist/index.js';

import {ExternalAppComponentProps} from '@flowable/work';

export type StarWarsTwitterAppProps = ExternalAppComponentProps & {};

export const StarWarsTwitterApp = (props: StarWarsTwitterAppProps) => {
    return (<TwitterTimelineEmbed
        sourceType="list"
        ownerScreenName="starwars"
        slug="star-wars-cast-crew"
        options={{height: 800}}
    />);
};
