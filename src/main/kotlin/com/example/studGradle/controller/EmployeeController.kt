package com.example.studGradle.controller

//import org.springframework.security.core.GrantedAuthority
//import org.springframework.security.core.authority.SimpleGrantedAuthority
import com.example.studGradle.DTO.CustomExceptionMessage
import com.example.studGradle.Modal.Employee
import com.example.studGradle.Modal.Token
import com.example.studGradle.config.CustomUserDetails
import com.example.studGradle.config.JwtTokenFilter
import com.example.studGradle.config.JwtTokenProvider
import com.example.studGradle.service.FileUploadHelper
import com.example.studGradle.service.TokenService
import com.example.studGradle.service.employeeService
import com.example.studGradle.valiadtion.ValidateEmployee
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("/employee")
class employeeController(
    private val fileUploadHelper: FileUploadHelper,
    private val checkValidation: ValidateEmployee,
    private val jwtTokenProvider: JwtTokenProvider,
    private val tokenService: TokenService,
    private val jwtTokenFilter: JwtTokenFilter,
    private val customeuserdetails: CustomUserDetails,
    private val EmployeeService: employeeService,
    private val  validateUser: CustomUserDetails
) {

    @GetMapping("/login")
    @CrossOrigin
    fun welcome(
        @RequestParam username: String,
        @RequestParam password: String,
    ): ResponseEntity<Any> {
        try {
            if (validateUser.isUserValid(username, password)) {
                val ganeratedToken = jwtTokenProvider.generateToken(username)
                val token = Token(token = ganeratedToken)
                tokenService.saveToken(token) //save token in database
                return ResponseEntity.ok(ganeratedToken)
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalide Credentials")
            }
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
        }
    }


    @GetMapping("/show")
    @CrossOrigin
    fun getAllEmployees(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "100") size: Int,
        @RequestParam(defaultValue = "Id") field: String
    ): ResponseEntity<Any> {
        val pageable = PageRequest.of(page, size, Sort.by(field))
        val employees = EmployeeService.getAllEmployees(pageable)
        return ResponseEntity.ok(employees)
    }

    @GetMapping("/logout")
    fun logout(request: HttpServletRequest): ResponseEntity<String> {
        try{
            val token = jwtTokenFilter.extractToken(request)
            if (token != null) {
                tokenService.deleteToken(token)
                return ResponseEntity.ok("logged out ...")
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token not Exist.")
        }catch (e:Exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
        }
    }

    @GetMapping("/{id}")
    fun getStudentById(@PathVariable id: Long): ResponseEntity<Any> {
        try{
            val employeeData= EmployeeService.getStudentById(id)
            return  ResponseEntity.ok().body(employeeData)
        }
        catch (e:CustomExceptionMessage){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
        catch (e:Exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
        }
    }

    @PostMapping("/add")
    fun saveStudent(@RequestBody employee: Employee): ResponseEntity<Any> {
        try{
            val result = checkValidation.isValide(employee)
            if (result.isValid) {
                EmployeeService.saveStudent(employee)
                return ResponseEntity.ok().body("New Employee Registared.")
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.errors)
            }
        }
        catch(e:CustomExceptionMessage){
          return   ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
        }
        catch (e:Exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
        }
    }

    @PatchMapping("/{id}/update-image")
    @CrossOrigin
    fun updateEmployeeImage(
        @PathVariable id: Long,
        @RequestParam("imageFile") imageFile: MultipartFile
    ): ResponseEntity<String> {
        try {
            fileUploadHelper.UploadFile(imageFile)
            EmployeeService.updateEmployeeImage(id, imageFile.bytes)
            return ResponseEntity.ok("Image updated successfully")
        }
        catch (e:CustomExceptionMessage){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
        catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
        }
    }

//    @PutMapping("/{id}")
//    @CrossOrigin
//    fun updateStudent(@PathVariable id:Long ,@RequestBody employee: Employee):
//            ResponseEntity<Any> {
//        return EmployeeService.updateStudent(id,employee)
//    }


    @DeleteMapping("/{id}")
    @CrossOrigin
    fun deleteStudent(@PathVariable id: Long): ResponseEntity<Any> {
        try{
            EmployeeService.deleteStudent(id)
            return ResponseEntity.ok().body("employee delete with id : $id")
        }
        catch (e:CustomExceptionMessage){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
        catch (e:Exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error")
        }

    }

    @PostMapping("/file-upload")
    @CrossOrigin
    fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<Any> {
        println(file.contentType)
        println(file.name)
        println(file.size)
        println(file.originalFilename)

        if (file.isEmpty) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request must contain file")
        } else {
            return ResponseEntity.ok("file get")
        }
    }


}