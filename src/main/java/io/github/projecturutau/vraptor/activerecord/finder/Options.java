package io.github.projecturutau.vraptor.activerecord.finder;

public class Options {
	private String attribute;
	private Object attributeValue;
	private String orderAtribute;
	private OrderEnum order;
	private boolean isOrdanable;

	public Options(String attribute, Object value, String orderAtribute, OrderEnum orderType) {
		this.attribute = attribute;
		this.attributeValue = value;
		this.orderAtribute = orderAtribute;
		this.order = orderType;
		setOrdanable(true);
	}

	public Options(String attribute, Object value, String orderAtribute) {
		this.attribute = attribute;
		this.attributeValue = value;
		this.orderAtribute = orderAtribute;
		// ASC is default
		this.order = OrderEnum.ASC;
		setOrdanable(true);
	}

	public Options(String attribute, Object value) {
		this.attribute = attribute;
		this.attributeValue = value;
		setOrdanable(false);
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public Object getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(Object attributeValue) {
		this.attributeValue = attributeValue;
	}

	public String getOrderAtribute() {
		return orderAtribute;
	}

	public void setOrderAtribute(String orderAtribute) {
		this.orderAtribute = orderAtribute;
	}

	public OrderEnum getOrder() {
		return order;
	}

	public void setOrder(OrderEnum order) {
		this.order = order;
	}

	public boolean isOrdanable() {
		return isOrdanable;
	}

	public void setOrdanable(boolean isOrdanable) {
		this.isOrdanable = isOrdanable;
	}
}
