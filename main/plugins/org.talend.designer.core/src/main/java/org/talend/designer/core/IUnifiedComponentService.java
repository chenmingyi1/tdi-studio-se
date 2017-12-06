package org.talend.designer.core;

import java.util.Collection;

import org.talend.core.IService;
import org.talend.core.model.components.IComponent;

// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

/**
 * created by wchen on Dec 6, 2017 Detailled comment
 *
 */
public interface IUnifiedComponentService extends IService {

    public boolean isUnifiedComponent(IComponent component);

    public String[] getUnifiedComponents(IComponent delegateComponet);

    public Collection<IComponent> getDelegateComponents(String componentCategory);
}
