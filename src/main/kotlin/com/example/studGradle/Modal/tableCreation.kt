package com.example.studGradle.Modal

import jakarta.persistence.*


@Entity
@Table(name = "Employee")
class Employee(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var name: String,
    var age: Long,
    var username: String,
    var password: String,
    @Lob // Use @Lob to specify a large object (for binary data like images)
    var image: ByteArray? = null, // Byte array to store the image data

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "department_id") // This specifies the foreign key column in Employee
    var department: Department? = null
) {
}