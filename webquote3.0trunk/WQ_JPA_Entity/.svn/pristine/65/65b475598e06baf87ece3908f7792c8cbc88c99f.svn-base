package com.avnet.emasia.webquote.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author Lin, Tough(901518)
 * Created on 2013-1-20
 */

@Entity
@DiscriminatorValue("SCREEN")
public class Screen extends Resource {
	
	@Column(name="PATH_ORDER", precision=5)
	private int pathOrder;

	public int getPathOrder() {
		return pathOrder;
	}

	public void setPathOrder(int pathOrder) {
		this.pathOrder = pathOrder;
	}

}
