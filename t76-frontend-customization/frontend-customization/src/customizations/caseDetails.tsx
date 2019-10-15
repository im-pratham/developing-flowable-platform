import React from 'react';

import {CaseInstance, DetailCustomizations, ExternalTab, TypeBadgeCustomization} from '@flowable/work';

async function caseTypeBadgeCustomization(element: CaseInstance): Promise<TypeBadgeCustomization> {
    return Promise.resolve({
        label: 'Case',
        iconName: 'bells',
        className: 'flo-typeBadge--bells'
    });
}

function caseTabsCustomization(element: CaseInstance): Promise<DetailCustomizations> {
    // Here it's a good place to do network requests, but keep in mind that case details won't render until that
    // request is completed. Element contains Case details, if you need more info in there ask the product team!
    const externalTabs: { [tabId: string]: ExternalTab } = {
        progress: {
            label: 'Progress',
            icon: 'tachometer',
            order: 1,
            component: () => <div/>
        }
    };

    const tabOverrides: { [tabId: string]: Partial<ExternalTab> } = {
        task: {
            order: 999,
            hidden: true
        },
        workForm: {
            hidden: true
        },
        people: {
            hidden: true
        },
        subItems: {
            hidden: true
        },
        history: {
            hidden: true
        }
    };

    const detailCustomizations = Promise.resolve({externalTabs, tabOverrides});
    return detailCustomizations;
}

export async function caseDetailCustomizations(element: Element): Promise<DetailCustomizations> {
    const [{externalTabs, tabOverrides}, typeBadge] = await Promise.all([
        caseTabsCustomization(element),
        caseTypeBadgeCustomization(element)
    ]);
    return {externalTabs, tabOverrides, typeBadge};
}
