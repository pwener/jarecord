package io.github.pwener.jarecord.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import io.github.pwener.jarecord.activerecord.ActiveRecord;

@Entity
public class Sample extends ActiveRecord<Sample> {
	@Id
	private Integer id;
	private String title;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
