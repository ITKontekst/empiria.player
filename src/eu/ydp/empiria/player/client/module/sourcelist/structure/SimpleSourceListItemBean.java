package eu.ydp.empiria.player.client.module.sourcelist.structure;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import eu.ydp.empiria.player.client.module.abstractmodule.structure.HasFixed;

@XmlRootElement(name = "simpleSourceListItem")
@XmlAccessorType(XmlAccessType.FIELD)
public class SimpleSourceListItemBean implements HasFixed{
	@XmlAttribute
	private String alt;

	@XmlAttribute
	private boolean fixed;

	@XmlValue
	private String value;

	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean isFixed() {
		return fixed;
	}

	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}
}
