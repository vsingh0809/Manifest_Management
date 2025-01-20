using ManifestPortal1.Business.Class;
using ManifestPortal1.Business.Contracts;
using ManifestPortal1.Entities.DTO;
using ManifestPortal1.Filters;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace ManifestPortal1.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    [ServiceFilter(typeof(LoggingActionFilter))]
    public class CrewsController : ControllerBase
    {
        private readonly ICrewService operatorService;

        private readonly ILogger<CrewsController> _logger;
       
        public CrewsController(ICrewService operatorService, ILogger<CrewsController> logger)
        {
            this.operatorService = operatorService;
            this._logger = logger;
        }

    

        /// <summary>
        /// Retrieves a list of all operators.
        /// </summary>
        /// <returns>
        /// Returns a list of all operators.
        /// </returns>
        /// <response code="200">Returns the list of operators.</response>
        /// <response code="400">If a bad request occurs.</response>
        [HttpGet]
        public IActionResult GetAllOperators()
        {
            _logger.LogInformation("GetAllOperators endpoint hit");
            var allOperators = operatorService.GetAllOperators();
            return Ok(allOperators);
        }



        /// <summary>
        /// Retrieves an operator by their unique identifier.
        /// </summary>
        /// <param name="id">The unique identifier of the operator.</param>
        /// <returns>
        /// Returns the operator details if found.
        /// </returns>
        /// <response code="200">Returns the operator details.</response>
        /// <response code="404">If no operator is found with the specified ID.</response>

        [HttpGet]
        [Route("{id:int}")]
        public IActionResult GetOperatorById(int id)
        {
            _logger.LogInformation("GetOperatorById endpoint hit");

            var operatorEntity = operatorService.GetOperatorById(id);
            if (operatorEntity == null)
            {
                return NotFound();
            }

            return Ok(operatorEntity);
        }


        /// <summary>
        /// Updates an operator's details by their unique identifier.
        /// </summary>
        /// <param name="id">The unique identifier of the operator to update.</param>
        /// <param name="newOperator">The data transfer object containing the updated operator information.</param>
        /// <returns>
        /// Returns the updated operator details.
        /// </returns>
        /// <response code="200">Returns the updated operator details.</response>
        /// <response code="400">If the updated operator data is invalid or missing.</response>
        /// <response code="404">If no operator is found with the specified ID.</response>


        [HttpPut]
        [Route("{id:int}")]
        public IActionResult UpdateOperator(int id, UpdateCrewoperatorDTO newOperator)
        {
            var updatedOperator = operatorService.UpdateOperator(id, newOperator);
            if (updatedOperator == null)
            {
                return NotFound();
            }
            return Ok(updatedOperator);
        }



        /// <summary>
        /// Registers a new operator.
        /// </summary>
        /// <param name="addOperator">The data transfer object containing the new operator's information.</param>
        /// <returns>
        /// Returns the newly created operator details.
        /// </returns>
        /// <response code="200">Returns the newly created operator details.</response>
        /// <response code="400">If the new operator data is invalid or missing.</response>


        [HttpPost]
        public async Task<IActionResult> RegisterCrewOperator(AddCrewoperatorDTO addOperator)
        {
            try
            {
                var Crewoperator = await operatorService.RegisterCrewOperator(addOperator);
                return Ok(Crewoperator);
            }
            catch (ArgumentException ex)
            {
                return BadRequest(new Dictionary<string, object>
                    {
                        { "error", "Invalid request" },
                        { "details", ex.Message }
                    });
            }
        }



        /// <summary>
        /// Updates the email address of an operator by their unique identifier.
        /// </summary>
        /// <param name="id">The unique identifier of the operator.</param>
        /// <param name="newEmail">The new email address to update.</param>
        /// <returns>
        /// Returns the updated operator details.
        /// </returns>
        /// <response code="200">Returns the updated operator details.</response>
        /// <response code="400">If the new email is invalid or missing.</response>
        /// <response code="404">If no operator is found with the specified ID.</response>


        //[HttpPut("update-email/{id:int}")]
        //public IActionResult UpdateEmail(int id, [FromBody] string newEmail)
        //{
        //    if (string.IsNullOrEmpty(newEmail))
        //    {
        //        return BadRequest();
        //    }

        //    var crew = operatorService.GetOperatorById(id);
        //    var checkEmail = operatorService.GetOperatorByEmail(newEmail);
        //    if (checkEmail != null) return BadRequest($"Email is already registerd to the user {checkEmail.FirstName} and Id : {checkEmail.CrewId}");
            
        //    if (crew == null) return NotFound();
        //    try
        //    {
        //        var updatedCrewoperator = operatorService.UpdateEmail(id, newEmail);
        //        return Ok(updatedCrewoperator);
        //    }
        //    catch (Exception ex)
        //    {
        //        // Log the exception if needed: _logger.LogError(ex, "Error updating email");
        //        return StatusCode(500, "An error occurred while updating the email.");
        //    }

        //}


        [HttpPut("update-email/{id:int}")]
        public IActionResult UpdateEmail(int id, [FromBody] string newEmail)
        {
            // Call the service layer to update the email
            var updatedOperator = operatorService.UpdateEmail(id, newEmail);

            if (updatedOperator == null)
            {
                // If null is returned, it means the email is already taken or operator not found
                return BadRequest(new { message = "The email is already in use ." });
            }

            // Return the updated operator if successful
            return Ok(updatedOperator);
        }



        /// <summary>
        /// Updates the password of an operator by their unique identifier.
        /// </summary>
        /// <param name="id">The unique identifier of the operator.</param>
        /// <param name="newPassword">The new password to update.</param>
        /// <returns>
        /// Returns the updated operator details.
        /// </returns>
        /// <response code="200">Returns the updated operator details.</response>
        /// <response code="400">If the new password is invalid or missing.</response>
        /// <response code="404">If no operator is found with the specified ID.</response>



        [HttpPut("update-password")]
        public IActionResult UpdatePassword([FromBody] UpdatePasswordRequestDTO request)
        {
            if (request == null)
            {
                return BadRequest();
            }
            try
            {
                var updatedCrewoperator = operatorService.UpdatePassword(request.Id, request.NewPassword);
                if (updatedCrewoperator == null)
                {
                    return NotFound();
                }
                return Ok(updatedCrewoperator);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "An error occurred while updating the password.");
            }
        }

        /// <summary>
        /// Verifies old password of operator.
        /// </summary>
        /// <param name="id">The unique identifier of the operator.</param>
        /// <param name="oldPassword">The old password to verify.</param>
        /// <returns>
        /// Returns the status code.
        /// </returns>
        /// <response code="200">Returns the updated operator details.</response>
        /// <response code="400">If the new password is invalid or missing.</response>
        /// <response code="404">If no operator is found with the specified ID.</response>


        [HttpPost("verify-old-password")]
        public IActionResult VerifyOldPassword([FromBody] PasswordVerificationRequest request)
        {
            // Check if request is null or OldPassword is empty
            if (request == null || string.IsNullOrEmpty(request.OldPassword) || request.Id <= 0)
            {
                return BadRequest("Old password or Id cannot be empty.");
            }

            // Verify old password with the InstId
            bool isPasswordValid = operatorService.VerifyOldPassword(request.OldPassword, request.Id);

            if (isPasswordValid)
            {
                return Ok(true); // Return success if password is valid
            }

            return BadRequest("Invalid or expired password.");
        }


        /// <summary>
        /// Updates the image of an operator by their unique identifier.
        /// </summary>
        /// <param name="id">The unique identifier of the operator.</param>
        /// <param name="newImage">The new image (byte array) to update.</param>
        /// <returns>
        /// Returns the updated operator details.
        /// </returns>
        /// <response code="200">Returns the updated operator details.</response>
        /// <response code="400">If the new image is invalid or missing.</response>
        /// <response code="404">If no operator is found with the specified ID.</response>


        //[HttpPut("update-image/{id:int}")]
        //public IActionResult UpdateImage(int id, [FromBody] byte[] newImage)
        //{
        //    var updatedCrewoperator = operatorService.UpdateImage(id, newImage);
        //    if (updatedCrewoperator == null)
        //    {
        //        return NotFound();
        //    }
        //    return Ok(updatedCrewoperator);
        //}

        [HttpPut("update-crew-image/{crewId}")]
        public async Task<IActionResult> UpdateCrewImage(int crewId, [FromBody] UpdateCrewImageRequestDTO request)
        {
            if (string.IsNullOrEmpty(request.ImageBase64))
            {
                return BadRequest("Image data is required.");
            }

            try
            {
                var result = await operatorService.UpdateCrewImageAsync(crewId, request);

                if (!result.Success)
                {
                    return result.StatusCode switch
                    {
                        404 => NotFound(result.Message),
                        400 => BadRequest(result.Message),
                        _ => StatusCode(500, result.Message)
                    };
                }

                return Ok(new { Message = result.Message });
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Internal server error: {ex.Message}");
            }
        }







        //------------------------------------------

        //JWT login



        /// <summary>
        /// Logs in a Crewoperator member/operator and returns a JWT token if the credentials are valid.
        /// </summary>
        /// <param name="loginDto">The data transfer object containing the login credentials (username and password).</param>
        /// <returns>
        /// Returns a JWT token if login is successful, or an error message if the credentials are invalid.
        /// </returns>
        /// <response code="200">Returns a JWT token if the login is successful.</response>
        /// <response code="400">If the login credentials are invalid.</response>

        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] LoginDTO loginDto)
        {
            try
            {
                if (loginDto == null)
                {
                    return BadRequest(new Dictionary<string, object> { { "Message", "Invalid request payload." } });
                }

                var token = await operatorService.LoginCrewoperatorAsync(loginDto);
                return Ok(new Dictionary<string, object> { { "Token", token } });
            }
            catch (KeyNotFoundException ex)
            {
                return NotFound(new Dictionary<string, object> { { "Message", ex.Message } }); // Email not found
            }
            catch (UnauthorizedAccessException ex)
            {
                return Unauthorized(new Dictionary<string, object> { { "Message", ex.Message } }); // Incorrect password
            }
            catch (ArgumentException ex)
            {
                return BadRequest(new Dictionary<string, object> { { "Message", ex.Message } });// Invalid request payload
            }
            catch (Exception ex)
            {
                return StatusCode(500, new Dictionary<string, object> { { "Message", "An unexpected error occurred." } });
            }
        }

        [HttpGet("ping")]
        public IActionResult Ping([FromQuery] string? msg = null)
        {
            var resp = string.IsNullOrEmpty(msg) ? "Ping serivce is runnig.." : $" Ping ! Recieved your msg  : {msg}";
            return Ok(new { message = msg });


        }

    }
}



