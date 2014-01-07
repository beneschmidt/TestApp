package test.app.dev.ueb07;

import java.io.Serializable;

public class Priority implements Serializable {

	private int _id;
	private String name;

	public Priority() {
	}

	public Priority(String name) {
		this.name = name;
	}

	public Priority(int _id, String name) {
		this._id = _id;
		this.name = name;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + _id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Priority other = (Priority) obj;
		if (_id != other._id)
			return false;
		return true;
	}

}
