package test.app.dev.ueb07;

import android.content.ContentValues;

public class SelectionArguments {
	private final String tableName;
	private String[] projection;
	private String selection;
	private String[] selectionArguments;
	private String sortOrder;
	private ContentValues values;

	public SelectionArguments(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}

	public String[] getProjection() {
		return projection;
	}

	public SelectionArguments setProjection(String[] projection) {
		this.projection = projection;
		return this;
	}

	public String getSelection() {
		return selection;
	}

	/**
	 * fügt eine weitere Selektion hinzu
	 * @param selection
	 * @return this
	 */
	public SelectionArguments addSelection(String selection) {
		if (this.selection == null)
			this.selection = selection;
		else if (selection != null)
			this.selection += " AND " + selection;
		return this;
	}

	public String[] getSelectionArguments() {
		return selectionArguments;
	}

	public SelectionArguments setSelectionArguments(String[] selectionArguments) {
		this.selectionArguments = selectionArguments;
		return this;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public SelectionArguments setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
		return this;
	}

	public ContentValues getValues() {
		return values;
	}

	public SelectionArguments setValues(ContentValues values) {
		this.values = values;
		return this;
	}

}
