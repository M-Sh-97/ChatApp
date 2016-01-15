import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author M-Sh-97
 */
class ContactTableModel extends DefaultTableModel {
  private Vector<Boolean> editableColumns;
  
  public ContactTableModel() {
    this(new Vector<>(0), 0, new Vector<>(0));
  }
  
  public ContactTableModel(Vector<String> columnNames, int rowCount) {
    this(columnNames, rowCount, new Vector<>(columnNames.size()));
  }
  
  public ContactTableModel(Vector<String> columnNames, int rowCount, Vector<Boolean> columnEditingPermissions) {
    super(columnNames, rowCount);
    editableColumns = columnEditingPermissions;
    int newSize = this.columnIdentifiers.size();
    editableColumns.setSize(newSize);
    for (int index = 0; index < newSize; index ++)
      if (editableColumns.get(index) == null)
	editableColumns.setElementAt(Boolean.FALSE, index);
  }
  
  @Override
  public Class<?> getColumnClass(int columnIndex) {
    return String.class;
  }
  
  @Override
  public boolean isCellEditable(int row, int column) {
    return editableColumns.get(column).booleanValue();
  }
  
  public void setEditableColumns(Vector<Boolean> columnEditingPermissions) {
    int q;
    if (columnEditingPermissions.size() > editableColumns.size())
      q = editableColumns.size();
    else
      q = columnEditingPermissions.size();
    Boolean element;
    for (int index = 0; index < q; index ++) {
      element = columnEditingPermissions.get(index);
      if (element != null)
	editableColumns.setElementAt(element, index);
    }
  }
  
  public void clearDataVector() {
    while (this.dataVector.size() > 0)
      super.removeRow(0);
  }
}
