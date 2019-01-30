package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the PRODUCT_CATEGORY database table.
 * 
 */
@Entity
@Table(name="PRODUCT_CATEGORY")
public class ProductCategory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="PRODUCT_CATEGORY_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PRODUCT_CATEGORY_ID_GENERATOR")
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="CATEGORY_CODE", nullable=false, length=10)
	private String categoryCode;

	@Column(name="\"VERSION\"", nullable=false)
	@Version
	private int version;

	public ProductCategory() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCategoryCode() {
		return this.categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((categoryCode == null) ? 0 : categoryCode.hashCode());
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
		ProductCategory other = (ProductCategory) obj;
		if (categoryCode == null) {
			if (other.categoryCode != null)
				return false;
		} else if (!categoryCode.equals(other.categoryCode))
			return false;
		return true;
	}
	
	

}