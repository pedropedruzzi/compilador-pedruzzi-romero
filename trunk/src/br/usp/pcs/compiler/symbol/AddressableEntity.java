package br.usp.pcs.compiler.entity;

public abstract class AddressableEntity extends Entity {
	
	protected String address;
	
	public AddressableEntity(String address) {
		this.address = address;
	}
	
	public String getAddress() {
		return address;
	}
	
}
