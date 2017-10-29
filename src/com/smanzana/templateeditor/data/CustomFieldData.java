package com.smanzana.templateeditor.data;

import java.util.LinkedList;
import java.util.List;

import com.smanzana.templateeditor.api.FieldData;
import com.smanzana.templateeditor.api.ICustomData;
import com.smanzana.templateeditor.editor.fields.EditorField;
import com.smanzana.templateeditor.editor.fields.GenericListField;

/**
 * FieldData for custom data types.
 * You should not be creating this by yourself. Use the static helper
 * functions in {@link FieldData}
 * @author Skyler
 *
 */
public final class CustomFieldData extends FieldData {
	
	private ICustomData dataLink;
	private List<ICustomData> dataList;
	private boolean isList;
	
	public CustomFieldData(ICustomData data) {
		this(data, null);
	}
	
	public CustomFieldData(ICustomData base, List<ICustomData> customlist) {
		this.dataLink = base;
		this.dataList = customlist;
		
		this.isList = customlist != null;
	}
	
	@Override
	public FieldData clone() {
		return new CustomFieldData(dataLink, dataList).name(getName()).desc(getDescription());
	}

	@Override
	public EditorField<?> constructField() {
		if (isList) {
			List<CustomFieldData> fields = new LinkedList<>();
			for (ICustomData d : dataList) {
				fields.add(new CustomFieldData(d));
			}
			return new GenericListField<CustomFieldData>(new CustomFieldData(dataLink), fields);
		} else
			return dataLink.getField();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void fillFromField(EditorField<?> field) {
		if (isList) {
			dataList.clear();
			List<CustomFieldData> retlist = ((GenericListField<CustomFieldData>) field).getObject();
			for (CustomFieldData d : retlist) {
				dataList.add(d.dataLink);
			}
		} else
			dataLink.fillFromField(field);
	}
	
	public ICustomData getData() {
		if (isList)
			return null;
		
		return dataLink;
	}
	
	public List<ICustomData> getDataList() {
		if (!isList)
			return null;
		
		return dataList;
	}
	
	public boolean isList() {
		return isList;
	}
}
