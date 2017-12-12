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
package org.talend.designer.core.utils;

import java.util.List;

import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.components.IComponent;
import org.talend.core.model.components.IComponentsService;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.process.INode;
import org.talend.designer.core.IUnifiedComponentService;
import org.talend.designer.core.model.components.EParameterName;
import org.talend.designer.core.ui.editor.nodes.Node;

/**
 * created by wchen on Dec 11, 2017 Detailled comment
 *
 */
public class UnifiedComponentUtil {

    public static IComponent getEmfComponent(Node node, IComponent component) {
        if (isDelegateComponent(component)) {
            IElementParameter elementParameter = node.getElementParameter(EParameterName.UNIFIED_COMPONENTS.name());
            if (elementParameter != null && elementParameter.getValue() != null) {
                String emfCompName = String.valueOf(elementParameter.getValue());
                String paletteType = component.getPaletteType();
                IComponentsService compService = (IComponentsService) GlobalServiceRegister.getDefault().getService(
                        IComponentsService.class);
                IComponent emfComponent = compService.getComponentsFactory().get(emfCompName, paletteType);
                if (emfComponent != null) {
                    return emfComponent;
                }
            }
        }
        return component;
    }

    public static boolean isDelegateComponent(IComponent component) {
        if (GlobalServiceRegister.getDefault().isServiceRegistered(IUnifiedComponentService.class)) {
            IUnifiedComponentService service = (IUnifiedComponentService) GlobalServiceRegister.getDefault().getService(
                    IUnifiedComponentService.class);
            if (service.isDelegateComponent(component)) {
                return true;
            }
        }
        return false;
    }

    public static IComponent getDelegateComponent(IComponent component) {
        if (GlobalServiceRegister.getDefault().isServiceRegistered(IUnifiedComponentService.class)) {
            IUnifiedComponentService service = (IUnifiedComponentService) GlobalServiceRegister.getDefault().getService(
                    IUnifiedComponentService.class);
            return service.getDelegateComponent(component);
        }
        return component;
    }

    public static void createParameters(INode node, List<IElementParameter> listParams, IComponent delegateComp,
            IComponent emfComp) {
        if (GlobalServiceRegister.getDefault().isServiceRegistered(IUnifiedComponentService.class)) {
            IUnifiedComponentService service = (IUnifiedComponentService) GlobalServiceRegister.getDefault().getService(
                    IUnifiedComponentService.class);
            service.createParameters(node, listParams, delegateComp, emfComp);
        }
    }

    public static void switchComponent(INode node, IComponent delegateComponent, List<? extends IElementParameter> oldParms) {
        if (GlobalServiceRegister.getDefault().isServiceRegistered(IUnifiedComponentService.class)) {
            IUnifiedComponentService service = (IUnifiedComponentService) GlobalServiceRegister.getDefault().getService(
                    IUnifiedComponentService.class);
            service.switchComponent(node, delegateComponent, oldParms);
        }

    }

}
