package com.biblioteca.app.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "User")
public class User {

	@Id
	@UuidGenerator
	@Column(name = "UserId", columnDefinition = "BINARY(16)", nullable = false)
	private UUID userId;

	@Column(name = "Email", length = 255, nullable = false, unique = true)
	private String email;

	@Column(name = "Password", length = 500, nullable = false)
	private String password;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "StatusId", nullable = false, foreignKey = @ForeignKey(name = "fk_user_status"))
	private Status status;

	@Column(name = "CreatedAt", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "UpdatedAt")
	private LocalDateTime updatedAt;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<UserRole> userRoles = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	// Constructores
	public User() {
	}

	public User(UUID userId, String email, String password, Status status) {
		this.userId = userId;
		this.email = email;
		this.password = password;
		this.status = status;
	}

	public User(UUID userId, String email, String password, Status status,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.userId = userId;
		this.email = email;
		this.password = password;
		this.status = status;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	// Getters y Setters
	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public List<UserRole> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(List<UserRole> userRoles) {
		this.userRoles = userRoles;
	}

	// Métodos helper para trabajar con roles

	/**
	 * Obtiene solo la lista de roles sin la entidad UserRole
	 * 
	 * @return Lista de roles del usuario
	 */
	public List<Role> getRoles() {
		return userRoles.stream()
				.map(UserRole::getRole)
				.collect(Collectors.toList());
	}

	/**
	 * Agrega un rol al usuario
	 * 
	 * @param role El rol a agregar
	 */
	public void addRole(Role role) {
		UserRole userRole = new UserRole(this, role);
		userRoles.add(userRole);
	}

	/**
	 * Remueve un rol del usuario
	 * 
	 * @param role El rol a remover
	 */
	public void removeRole(Role role) {
		userRoles.removeIf(ur -> ur.getRole().equals(role));
	}

	/**
	 * Verifica si el usuario tiene un rol específico
	 * 
	 * @param roleName Nombre del rol a verificar
	 * @return true si el usuario tiene el rol, false en caso contrario
	 */
	public boolean hasRole(String roleName) {
		return userRoles.stream()
				.anyMatch(ur -> ur.getRole().getRoleName().equalsIgnoreCase(roleName));
	}

	/**
	 * Limpia todos los roles del usuario
	 */
	public void clearRoles() {
		userRoles.clear();
	}

	@Override
	public String toString() {
		return email;
	}
}