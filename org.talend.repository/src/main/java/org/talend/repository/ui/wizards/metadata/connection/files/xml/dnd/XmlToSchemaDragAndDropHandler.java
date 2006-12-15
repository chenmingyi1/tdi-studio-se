// ============================================================================
//
// Talend Community Edition
//
// Copyright (C) 2006 Talend - www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
// ============================================================================
package org.talend.repository.ui.wizards.metadata.connection.files.xml.dnd;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.datatools.enablement.oda.xml.util.ui.XPathPopulationUtil;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.talend.commons.ui.swt.advanced.dataeditor.commands.ExtendedTableAddCommand;
import org.talend.commons.ui.swt.extended.table.ExtendedTableModel;
import org.talend.commons.ui.swt.tableviewer.TableViewerCreatorColumn;
import org.talend.commons.ui.utils.TableUtils;
import org.talend.core.model.metadata.builder.connection.SchemaTarget;
import org.talend.core.model.metadata.builder.connection.XmlXPathLoopDescriptor;
import org.talend.core.model.targetschema.editor.XmlExtractorFieldModel;
import org.talend.repository.ui.wizards.metadata.connection.files.xml.ExtractionFieldsWithXPathEditorView;
import org.talend.repository.ui.wizards.metadata.connection.files.xml.ExtractionLoopWithXPathEditorView;
import org.talend.repository.ui.wizards.metadata.connection.files.xml.XmlToXPathLinker;

/**
 * DOC amaumont class global comment. Detailled comment <br/>
 * 
 * $Id$
 * 
 */
public class XmlToSchemaDragAndDropHandler {

    private XmlToXPathLinker linker;

    private DragSource dragSource;

    private DropTarget fieldsDropTarget;

    protected int dropDefaultOperation = DND.DROP_LINK;

    private Tree tree;

    private Table fieldsTable;

    private Table loopTable;

    private DropTarget loopDropTarget;

    /**
     * DOC amaumont TreeToTableDragAndDropHandler constructor comment.
     * 
     * @param linker
     */
    public XmlToSchemaDragAndDropHandler(XmlToXPathLinker linker) {
        this.linker = linker;
        tree = linker.getTree();
        loopTable = linker.getLoopTableEditorView().getTableViewerCreator().getTable();
        fieldsTable = linker.getFieldsTableEditorView().getTableViewerCreator().getTable();
        init();
    }

    /**
     * DOC amaumont Comment method "init".
     */
    private void init() {
        createDragSource();
        createDropTarget();
    }

    /**
     * 
     * DOC amaumont Comment method "createDragSource".
     * 
     * @param sourceListener
     */
    private void createDragSource() {
        if (dragSource != null) {
            dragSource.dispose();
        }
        dragSource = new DragSource(tree, DND.DROP_DEFAULT | DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK);
        dragSource.setTransfer(new Transfer[] { XPathTransfer.getInstance() });
        DragSourceListener sourceListener = new TreeDragSourceListener();
        dragSource.addDragListener(sourceListener);
    }

    /**
     * 
     * create DropTarget.
     */
    private void createDropTarget() {

        if (loopDropTarget != null) {
            loopDropTarget.dispose();
        }
        loopDropTarget = new DropTarget(loopTable, DND.DROP_DEFAULT | DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK);
        loopDropTarget.setTransfer(new Transfer[] { XPathTransfer.getInstance() });
        DropTargetListener targetListener = new TableDropTargetListener();
        loopDropTarget.addDropListener(targetListener);

        if (fieldsDropTarget != null) {
            fieldsDropTarget.dispose();
        }
        fieldsDropTarget = new DropTarget(fieldsTable, DND.DROP_DEFAULT | DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK);
        fieldsDropTarget.setTransfer(new Transfer[] { XPathTransfer.getInstance() });
        targetListener = new TableDropTargetListener();
        fieldsDropTarget.addDropListener(targetListener);
    }

    /**
     * 
     * DOC amaumont XmlToSchemaDragAndDropHandler class global comment. Detailled comment <br/>
     * 
     * $Id$
     * 
     */
    class TreeDragSourceListener implements TransferDragSourceListener {

        public void dragFinished(DragSourceEvent event) {
        }

        public void dragSetData(DragSourceEvent event) {
            System.out.println("\n>>dragSetData");
            // System.out.println(event);
            // if (TableEntriesTransfer.getInstance().isSupportedType(event.dataType)) {
            // }
        }

        public void dragStart(DragSourceEvent event) {
            System.out.println("\n>>dragStart");
            // System.out.println(event);
            TreeItem[] items = tree.getSelection();
            if (items.length == 0) {
                event.doit = false;
            } else {
                XmlToSchemaDraggedData draggedData = new XmlToSchemaDraggedData();
                for (TreeItem treeItem : items) {
                    String absoluteXPath = linker.getAbsoluteXPath(treeItem);
                    draggedData.add(new TransferableXPathEntry(absoluteXPath));
                }
                XPathTransfer.getInstance().setDraggedData(draggedData);

            }
        }

        public Transfer getTransfer() {
            return XPathTransfer.getInstance();
        }
    };

    /**
     * 
     * DOC amaumont XmlToSchemaDragAndDropHandler class global comment. Detailled comment <br/>
     * 
     * $Id$
     * 
     */
    public class TableDropTargetListener implements TransferDropTargetListener {

        public void dragEnter(DropTargetEvent event) {
            dragEnterExecute(event);
        }

        /**
         * DOC amaumont Comment method "dragEnterExecute".
         * 
         * @param event
         */
        private void dragEnterExecute(DropTargetEvent event) {
            fieldsTable.setFocus();
        }

        public void dragOver(DropTargetEvent event) {
            System.out.println("\n>>dragOver");

        }

        public void dragLeave(DropTargetEvent event) {
            System.out.println("\n>>dragLeave");
            System.out.println(event);
        }

        public void dragOperationChanged(DropTargetEvent event) {
            System.out.println("\n>>dragOperationChanged");
            // showInfos(event);
            XmlToSchemaDraggedData draggedData = XPathTransfer.getInstance().getDraggedData();

        }

        public void dropAccept(DropTargetEvent event) {
            System.out.println("\n>>dropAccept");
            System.out.println(event);
            XmlToSchemaDraggedData draggedData = XPathTransfer.getInstance().getDraggedData();

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.util.TransferDropTargetListener#getTransfer()
         */
        public Transfer getTransfer() {
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.util.TransferDropTargetListener#isEnabled(org.eclipse.swt.dnd.DropTargetEvent)
         */
        public boolean isEnabled(DropTargetEvent event) {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.swt.dnd.DropTargetListener#drop(org.eclipse.swt.dnd.DropTargetEvent)
         */
        public void drop(DropTargetEvent event) {
            System.out.println("\n>>drop");
            DropTarget dropTarget = (DropTarget) event.getSource();
            Control control = dropTarget.getControl();

            XmlToSchemaDraggedData draggedData = XPathTransfer.getInstance().getDraggedData();

            List<TransferableXPathEntry> transferableEntryList = draggedData.getTransferableEntryList();

            ExtractionLoopWithXPathEditorView loopTableEditorView = linker.getLoopTableEditorView();
            ExtendedTableModel<XmlXPathLoopDescriptor> extendedTableModel = loopTableEditorView.getExtendedTableModel();
            XmlXPathLoopDescriptor pathLoopDescriptor = extendedTableModel.getBeansList().get(0);

            if (linker.isLoopTable((Table) control)) {

                if (transferableEntryList.size() > 0) {

                    String absoluteXPath = transferableEntryList.get(0).getAbsoluteXPath();

                    TableViewerCreatorColumn xpathColumn = linker.getLoopTableEditorView().getXPathColumn();
                    Display display = linker.getTree().getDisplay();
                    Cursor cursor = new Cursor(display, SWT.CURSOR_WAIT);
                    linker.getTree().getShell().setCursor(cursor);
                    loopTableEditorView.getTableViewerCreator().setBeanValue(xpathColumn, pathLoopDescriptor, absoluteXPath, true);
                    linker.getTree().getShell().setCursor(null);
                }

            } else {

                ExtractionFieldsWithXPathEditorView tableEditorView = linker.getFieldsTableEditorView();
                Integer startInsertAtThisIndex = TableUtils.getItemIndexWhereInsertFromPosition(fieldsTable, new Point(event.x, event.y));
                List<SchemaTarget> list = new ArrayList<SchemaTarget>(transferableEntryList.size());
                for (TransferableXPathEntry entry : transferableEntryList) {

                    ArrayList<String> loopXpathNodes = linker.getLoopXpathNodes();
                    if (loopXpathNodes.size() > 0) {
                        String loopPath = loopXpathNodes.get(0);
                        String relativeXPath = XPathPopulationUtil.populateColumnPath(loopPath, entry.getAbsoluteXPath());

                        if (relativeXPath.startsWith("/")) {
                            relativeXPath = relativeXPath.substring(1);
                        }
                        if (relativeXPath.endsWith("../")) {
                            relativeXPath = relativeXPath.substring(0, relativeXPath.length() - 1);
                        }
                        if (relativeXPath.equals("")) {
                            relativeXPath = ".";
                        }

                        SchemaTarget newTargetEntry = linker.getNewSchemaTargetEntry(relativeXPath);
                        list.add(newTargetEntry);
                    }
                }

                tableEditorView.getTableViewerCreator().getTableViewer().refresh();

                loopTable.deselectAll();
                fieldsTable.deselectAll();

                linker.getTree().deselectAll();

                if (list.size() > 0) {
                    ExtendedTableAddCommand addCommand = new ExtendedTableAddCommand(tableEditorView.getModel(), list,
                            startInsertAtThisIndex);

                    tableEditorView.getExtendedTableViewer().executeCommand(addCommand);
                }

            }
            linker.updateLinksStyleAndControlsSelection(control);
        }

    }

    public static void main(String[] args) {
        String relativePath = XPathPopulationUtil.populateColumnPath("/doc/members/member/returns", "/doc/members");
        System.out.println(relativePath);
        relativePath = XPathPopulationUtil.populateColumnPath("/doc/members/member/returns/see/@cref", "/doc/members/member/summary/@name");
        System.out.println(relativePath);
    }
}
