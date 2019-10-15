import {DetailCustomizations, DetailCustomizationType} from '@flowable/work';

import {caseDetailCustomizations} from './caseDetails';

export const detailCustomizations = (element: any, type: DetailCustomizationType): Promise<DetailCustomizations> => {

    if (type === 'case') {
        return caseDetailCustomizations(element);
    }
    return Promise.resolve({externalTabs: {}, tabOverrides: {}});
};
