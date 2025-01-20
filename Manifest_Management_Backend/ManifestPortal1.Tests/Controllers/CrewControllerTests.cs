

using System;
using System.Threading.Tasks;
using ManifestPortal1.Business.Contracts;
using ManifestPortal1.Entities.DTO;
using ManifestPortal1.Controllers;
using Microsoft.AspNetCore.Mvc;
using Moq;
using Xunit;
using FluentAssertions;
using ManifestPortal1.Entities.Models;
using ManifestPortal1.DataAccess.Repository.Contracts;
using Microsoft.Extensions.Logging;
using FluentAssertions.Common;

namespace ManifestPortal1.Tests.Controllers
{
    public class CrewOperatorControllerTests
    {
        private readonly Mock<ICrewService> _operatorServiceMock;
        private readonly CrewsController _controller;

        public CrewOperatorControllerTests()
        {
            _operatorServiceMock = new Mock<ICrewService>();
            _controller = new CrewsController(_operatorServiceMock.Object, Mock.Of<ILogger<CrewsController>>());
        }

        [Fact]
        public async Task Login_ValidRequest_ReturnsOk()
        {
            // Arrange: Create a mock LoginDTO object with valid credentials
            var loginDto = new LoginDTO
            {
                email = "test@domain.com",
                password = "correctPassword"
            };

            var token = "mockToken";  // Mock a token

            // Setup the mock to return a token when LoginCrewoperatorAsync is called
            _operatorServiceMock.Setup(service => service.LoginCrewoperatorAsync(loginDto))
                .ReturnsAsync(token);

            // Act: Call the Login action method in the controller
            var result = await _controller.Login(loginDto);

            // Assert: Verify that the result is an OkObjectResult (200 response)
            var okResult = Assert.IsType<OkObjectResult>(result);

            // Assert that the value returned is a Dictionary<string, object>
            var value = Assert.IsType<Dictionary<string, object>>(okResult.Value);

            // Assert that the "Token" key contains the expected token value
            Assert.Equal(token, value["Token"]);
        }


        [Fact]
        public async Task Login_InvalidRequest_ReturnsBadRequest()
        {
            // Arrange: Create a LoginDTO object with invalid data (e.g., empty email and password)
            var loginDto = new LoginDTO { email = "", password = "" };

            // Setup the mock to throw ArgumentException with the appropriate message
            _operatorServiceMock.Setup(service => service.LoginCrewoperatorAsync(loginDto))
                .ThrowsAsync(new ArgumentException("Invalid request payload."));

            // Act: Call the Login action method in the controller
            var result = await _controller.Login(loginDto);

            // Assert: Verify that the result is a BadRequestObjectResult (400 response)
            var badRequestResult = Assert.IsType<BadRequestObjectResult>(result);

            // Assert that the response contains a dictionary with the "Message" key
            var value = Assert.IsType<Dictionary<string, object>>(badRequestResult.Value);

            // Assert that the "Message" key contains the expected error message
            Assert.Equal("Invalid request payload.", value["Message"]);
        }

        [Fact]
        public async Task Login_EmailNotFound_ReturnsNotFound()
        {
            // Arrange: Create a mock LoginDTO object with invalid credentials (non-existing email)
            var loginDto = new LoginDTO
            {
                email = "nonexistent@domain.com",  // This email does not exist in the database
                password = "password"
            };

            // Mock the LoginCrewoperatorAsync method to throw a KeyNotFoundException when an invalid email is provided
            _operatorServiceMock.Setup(service => service.LoginCrewoperatorAsync(loginDto))
                .ThrowsAsync(new KeyNotFoundException("Email not found"));

            // Act: Call the Login action method in the controller
            var result = await _controller.Login(loginDto);

            // Assert: Verify that the result is a NotFoundObjectResult (404 response)
            var notFoundResult = Assert.IsType<NotFoundObjectResult>(result);

            // Assert that the value contains the expected "Message" key with the correct error message
            var value = Assert.IsType<Dictionary<string, object>>(notFoundResult.Value);

            // Assert that the value contains the expected "Message" key with the correct error message
            Assert.Equal("Email not found", value["Message"]);
        }


        [Fact]
        public async Task Login_InvalidPassword_ReturnsUnauthorized()
        {
            // Arrange: Create a mock LoginDTO object with invalid credentials
            var loginDto = new LoginDTO
            {
                email = "test@domain.com",
                password = "invalidpassword"
            };

            // Arrange: Mock the LoginCrewoperatorAsync method to throw UnauthorizedAccessException
            _operatorServiceMock.Setup(service => service.LoginCrewoperatorAsync(It.IsAny<LoginDTO>()))
                .ThrowsAsync(new UnauthorizedAccessException("Invalid password"));

            // Act: Call the Login action method in the controller
            var result = await _controller.Login(loginDto);

            // Assert: Verify that the result is an UnauthorizedObjectResult (401 response)
            var unauthorizedResult = Assert.IsType<UnauthorizedObjectResult>(result);

            // Assert that the response is a Dictionary<string, object>
            var response = Assert.IsType<Dictionary<string, object>>(unauthorizedResult.Value);

            // Assert that the response contains a "Message" key with the correct error message
            Assert.Equal("Invalid password", response["Message"]);
        }

        [Fact]
        public async Task Login_InvalidArgument_ReturnsBadRequest()
        {
            // Arrange
            var loginDto = new LoginDTO { email = "", password = "" }; // Invalid credentials

            // Setup the mock to throw ArgumentException
            _operatorServiceMock.Setup(service => service.LoginCrewoperatorAsync(loginDto))
                .ThrowsAsync(new ArgumentException("Email and Password are required."));

            // Act: Call the Login action method in the controller
            var result = await _controller.Login(loginDto);

            // Assert: Verify that the result is a BadRequestObjectResult (400 response)
            var badRequestResult = Assert.IsType<BadRequestObjectResult>(result);

            // Assert that the response value is of type Dictionary<string, object>
            var value = Assert.IsType<Dictionary<string, object>>(badRequestResult.Value);

            // Assert that the value contains the expected "Message" key with the correct error message
            Assert.Equal("Email and Password are required.", value["Message"]);
        }

        [Fact]
        public async Task Login_UnhandledException_ReturnsServerError()
        {
            // Arrange: Create a mock LoginDTO object with any credentials
            var loginDto = new LoginDTO
            {
                email = "test@domain.com",
                password = "anyPassword"
            };

            // Arrange: Mock the LoginCrewoperatorAsync method to throw an unhandled exception
            _operatorServiceMock.Setup(service => service.LoginCrewoperatorAsync(loginDto))
                .ThrowsAsync(new Exception("Some unhandled error"));

            // Act: Call the Login action method in the controller
            var result = await _controller.Login(loginDto);

            // Assert: Verify that the result is a StatusCodeResult with status 500
            var statusCodeResult = Assert.IsType<ObjectResult>(result);
            Assert.Equal(500, statusCodeResult.StatusCode);

            // Assert that the response contains a dictionary with a "Message" key
            var value = Assert.IsType<Dictionary<string, object>>(statusCodeResult.Value);

            // Assert that the value contains the expected "Message" key with the correct error message
            Assert.Equal("An unexpected error occurred.", value["Message"]);
        }




        [Fact]
        public void GetAllOperators_ReturnsOk()
        {
            // Arrange: Mock a list of operators to be returned
            var mockOperators = new List<Crewoperator>
    {
        new Crewoperator { Salutation  = "Mr", FirstName = "Devesh" },
        new Crewoperator {Salutation = "Mr", FirstName = "Honey"}
    };

            _operatorServiceMock.Setup(service => service.GetAllOperators()).Returns((IEnumerable<Crewoperator>)mockOperators);

            // Act: Call the GetAllOperators action method
            var result = _controller.GetAllOperators();

            // Assert: Verify that the result is an OkObjectResult (200 response)
            var okResult = Assert.IsType<OkObjectResult>(result);

            // Assert that the value returned matches the mocked operators
            var value = Assert.IsType<List<Crewoperator>>(okResult.Value);
            value.Should().BeEquivalentTo(mockOperators);
        }

        [Fact]
        public void GetOperatorById_ExistingId_ReturnsOk()
        {
            // Arrange: Mock a single operator to be returned
            var mockOperator = new Crewoperator { CrewId = 1000, FirstName = "Honey" };

            _operatorServiceMock.Setup(service => service.GetOperatorById(1)).Returns(mockOperator);

            // Act: Call the GetOperatorById action method
            var result = _controller.GetOperatorById(1);

            // Assert: Verify that the result is an OkObjectResult (200 response)
            var okResult = Assert.IsType<OkObjectResult>(result);

            // Assert that the value returned matches the mocked operator
            var value = Assert.IsType<Crewoperator>(okResult.Value);
            value.Should().BeEquivalentTo(mockOperator);
        }

        [Fact]
        public void GetOperatorById_NonExistingId_ReturnsNotFound()
        {
            // Arrange: Mock the service to return null for a non-existing ID
            _operatorServiceMock.Setup(service => service.GetOperatorById(It.IsAny<int>())).Returns((Crewoperator)null);

            // Act: Call the GetOperatorById action method
            var result = _controller.GetOperatorById(99);

            // Assert: Verify that the result is a NotFoundResult (404 response)
            Assert.IsType<NotFoundResult>(result);
        }

        [Fact]
        public void UpdateOperator_ValidId_ReturnsOk()
        {
            // Arrange: Mock an updated operator to be returned
            var updateDTO = new UpdateCrewoperatorDTO { FirstName = "UpdatedOperator" };
            var updatedOperator = new Crewoperator { CrewId = 1, FirstName = "UpdatedOperator" };

            _operatorServiceMock.Setup(service => service.UpdateOperator(1, updateDTO)).Returns(updatedOperator);

            // Act: Call the UpdateOperator action method
            var result = _controller.UpdateOperator(1, updateDTO);

            // Assert: Verify that the result is an OkObjectResult (200 response)
            var okResult = Assert.IsType<OkObjectResult>(result);

            // Assert that the value returned matches the updated operator
            var value = Assert.IsType<Crewoperator>(okResult.Value);
            value.Should().BeEquivalentTo(updatedOperator);
        }

        [Fact]
        public void UpdateOperator_InvalidId_ReturnsNotFound()
        {
            // Arrange: Mock the service to return null for an invalid ID
            var updateDTO = new UpdateCrewoperatorDTO { FirstName = "UpdatedOperator" };

            _operatorServiceMock.Setup(service => service.UpdateOperator(It.IsAny<int>(), updateDTO)).Returns((Crewoperator)null);

            // Act: Call the UpdateOperator action method
            var result = _controller.UpdateOperator(99, updateDTO);

            // Assert: Verify that the result is a NotFoundResult (404 response)
            Assert.IsType<NotFoundResult>(result);
        }

        [Fact]
        public async Task RegisterCrewOperator_ValidRequest_ReturnsOk()
        {
            // Arrange: Mock a new operator to be returned
            var newOperator = new AddCrewoperatorDTO { FirstName = "NewOperator" };
            var mockOperator = new Crewoperator { CrewId = 1, FirstName = "NewOperator" };

            _operatorServiceMock.Setup(service => service.RegisterCrewOperator(newOperator)).ReturnsAsync(mockOperator);

            // Act: Call the RegisterCrewOperator action method
            var result = await _controller.RegisterCrewOperator(newOperator);

            // Assert: Verify that the result is an OkObjectResult (200 response)
            var okResult = Assert.IsType<OkObjectResult>(result);

            // Assert that the value returned matches the new operator
            var value = Assert.IsType<Crewoperator>(okResult.Value);
            value.Should().BeEquivalentTo(mockOperator);
        }





        [Fact]
        public async Task RegisterCrewOperator_InvalidRequest_ReturnsBadRequest()
        {
            // Arrange
            var invalidOperator = new AddCrewoperatorDTO { FirstName = null }; // Example of invalid data
            _operatorServiceMock
                .Setup(service => service.RegisterCrewOperator(invalidOperator))
                .ThrowsAsync(new ArgumentException("Invalid request"));

            // Act
            var result = await _controller.RegisterCrewOperator(invalidOperator);

            // Assert
            var badRequestResult = Assert.IsType<BadRequestObjectResult>(result);
            Assert.NotNull(badRequestResult);

            var value = badRequestResult.Value as Dictionary<string, object>;
            Assert.NotNull(value);
            Assert.Equal("Invalid request", value["error"]);
            Assert.Equal("Invalid request", value["details"]);
        }

        [Fact]
        public void UpdateEmail_ValidId_ReturnsUpdatedCrewOperator()
        {
            // Arrange
            int operatorId = 1;
            string newEmail = "newemail@example.com";
            var updatedCrewoperator = new Crewoperator { CrewId = operatorId, Email = newEmail };

            _operatorServiceMock
                .Setup(service => service.GetOperatorById(operatorId))
                .Returns(new Crewoperator { CrewId = operatorId, Email = "oldemail@example.com" });

            _operatorServiceMock
                .Setup(service => service.UpdateEmail(operatorId, newEmail))
                .Returns(updatedCrewoperator);

            // Act
            var result = _controller.UpdateEmail(operatorId, newEmail) as OkObjectResult;

            // Assert
            Assert.NotNull(result);
            Assert.Equal(200, result.StatusCode);
            Assert.Equal(updatedCrewoperator, result.Value);
        }

        [Fact]
        public void UpdateEmail_InvalidId_ReturnsNotFound()
        {
            // Arrange
            int operatorId = 99;
            string newEmail = "newemail@example.com";

            _operatorServiceMock
                .Setup(service => service.GetOperatorById(operatorId))
                .Returns((Crewoperator)null);

            // Act
            var result = _controller.UpdateEmail(operatorId, newEmail);

            // Assert
            Assert.IsType<NotFoundResult>(result);
        }



        [Fact]
        public void UpdateEmail_NullEmail_ReturnsBadRequest()
        {
            // Arrange
            int operatorId = 1;
            string newEmail = null;

            // Act
            var result = _controller.UpdateEmail(operatorId, newEmail);

            // Assert
            Assert.IsType<BadRequestResult>(result);
        }

        [Fact]
        public void UpdateEmail_EmptyEmail_ReturnsBadRequest()
        {
            // Arrange
            int operatorId = 1;
            string newEmail = "";

            // Act
            var result = _controller.UpdateEmail(operatorId, newEmail);

            // Assert
            Assert.IsType<BadRequestResult>(result);
        }

        [Fact]
        public void UpdateEmail_ServiceThrowsException_ReturnsInternalServerError()
        {
            // Arrange
            int operatorId = 1;
            string newEmail = "newemail@example.com";

            _operatorServiceMock
                .Setup(service => service.GetOperatorById(operatorId))
                .Returns(new Crewoperator { CrewId = operatorId, Email = "oldemail@example.com" });

            _operatorServiceMock
                .Setup(service => service.UpdateEmail(operatorId, newEmail))
                .Throws(new Exception("Database error"));

            // Act
            var result = _controller.UpdateEmail(operatorId, newEmail) as ObjectResult;

            // Assert
            Assert.NotNull(result);
            Assert.Equal(500, result.StatusCode);
            Assert.Equal("An error occurred while updating the email.", result.Value);
        }

        [Fact]
        public void UpdatePassword_ValidRequest_ReturnsUpdatedCrewOperator()
        {
            // Arrange
            var request = new UpdatePasswordRequestDTO { Id = 1, NewPassword = "NewPassword123" };
            var updatedCrewoperator = new Crewoperator { CrewId = request.Id, Password = "HashedNewPassword123" };

            _operatorServiceMock
                .Setup(service => service.UpdatePassword(request.Id, request.NewPassword))
                .Returns(updatedCrewoperator);

            // Act
            var result = _controller.UpdatePassword(request) as OkObjectResult;

            // Assert
            Assert.NotNull(result);
            Assert.Equal(200, result.StatusCode);
            Assert.Equal(updatedCrewoperator, result.Value);
        }

        [Fact]
        public void UpdatePassword_InvalidId_ReturnsNotFound()
        {
            // Arrange
            var request = new UpdatePasswordRequestDTO { Id = 99, NewPassword = "NewPassword123" };

            _operatorServiceMock
                .Setup(service => service.UpdatePassword(request.Id, request.NewPassword))
                .Returns((Crewoperator)null);

            // Act
            var result = _controller.UpdatePassword(request);

            // Assert
            Assert.IsType<NotFoundResult>(result);
        }

        [Fact]
        public void UpdatePassword_NullRequest_ReturnsBadRequest()
        {
            // Act
            var result = _controller.UpdatePassword(null);

            // Assert
            Assert.IsType<BadRequestResult>(result);
        }

        [Fact]
        public void UpdatePassword_ServiceThrowsException_ReturnsInternalServerError()
        {
            // Arrange
            var request = new UpdatePasswordRequestDTO { Id = 1, NewPassword = "NewPassword123" };

            _operatorServiceMock
                .Setup(service => service.UpdatePassword(request.Id, request.NewPassword))
                .Throws(new Exception("Database error"));

            // Act
            var result = _controller.UpdatePassword(request) as ObjectResult;

            // Assert
            Assert.NotNull(result);
            Assert.Equal(500, result.StatusCode);
            Assert.Equal("An error occurred while updating the password.", result.Value);
        }

        [Fact]
        public void VerifyOldPassword_ReturnsBadRequest_WhenRequestIsNull()
        {
            // Arrange
            PasswordVerificationRequest request = null;

            // Act
            var result = _controller.VerifyOldPassword(request);

            // Assert
            var badRequestResult = Assert.IsType<BadRequestObjectResult>(result);
            Assert.Equal("Old password or Id cannot be empty.", badRequestResult.Value);
        }

        [Fact]
        public void VerifyOldPassword_ReturnsBadRequest_WhenOldPasswordIsEmpty()
        {
            // Arrange
            var request = new PasswordVerificationRequest { OldPassword = "", Id = 1 };

            // Act
            var result = _controller.VerifyOldPassword(request);

            // Assert
            var badRequestResult = Assert.IsType<BadRequestObjectResult>(result);
            Assert.Equal("Old password or Id cannot be empty.", badRequestResult.Value);
        }

        [Fact]
        public void VerifyOldPassword_ReturnsBadRequest_WhenIdIsInvalid()
        {
            // Arrange
            var request = new PasswordVerificationRequest { OldPassword = "password", Id = 0 };

            // Act
            var result = _controller.VerifyOldPassword(request);

            // Assert
            var badRequestResult = Assert.IsType<BadRequestObjectResult>(result);
            Assert.Equal("Old password or Id cannot be empty.", badRequestResult.Value);
        }

        [Fact]
        public void VerifyOldPassword_ReturnsOk_WhenPasswordIsValid()
        {
            // Arrange
            var request = new PasswordVerificationRequest { OldPassword = "password", Id = 1 };
            _operatorServiceMock.Setup(service => service.VerifyOldPassword(It.IsAny<string>(), It.IsAny<int>())).Returns(true);

            // Act
            var result = _controller.VerifyOldPassword(request);

            // Assert
            var okResult = Assert.IsType<OkObjectResult>(result);
            Assert.True((bool)okResult.Value);
        }

        [Fact]
        public void VerifyOldPassword_ReturnsBadRequest_WhenPasswordIsInvalid()
        {
            // Arrange
            var request = new PasswordVerificationRequest { OldPassword = "wrongpassword", Id = 1 };
            _operatorServiceMock.Setup(service => service.VerifyOldPassword(It.IsAny<string>(), It.IsAny<int>())).Returns(false);

            // Act
            var result = _controller.VerifyOldPassword(request);

            // Assert
            var badRequestResult = Assert.IsType<BadRequestObjectResult>(result);
            Assert.Equal("Invalid or expired password.", badRequestResult.Value);
        }


        //[Fact]
        //public void UpdateImage_ReturnsNotFound_WhenOperatorNotFound()
        //{
        //    // Arrange
        //    var id = 1;
        //    var newImage = new byte[] { 1, 2, 3 };
        //    _operatorServiceMock.Setup(service => service.UpdateImage(id, newImage)).Returns((Crewoperator)null);

        //    // Act
        //    var result = _controller.UpdateImage(id, newImage);

        //    // Assert
        //    Assert.IsType<NotFoundResult>(result);
        //}

        //[Fact]
        //public void UpdateImage_ReturnsOk_WhenOperatorIsUpdated()
        //{
        //    // Arrange
        //    var id = 1;
        //    var newImage = new byte[] { 1, 2, 3 };
        //    var updatedOperator = new Crewoperator { CrewId = id, CrewImage = newImage };
        //    _operatorServiceMock.Setup(service => service.UpdateImage(id, newImage)).Returns(updatedOperator);

        //    // Act
        //    var result = _controller.UpdateImage(id, newImage);

        //    // Assert
        //    var okResult = Assert.IsType<OkObjectResult>(result);
        //    Assert.Equal(updatedOperator, okResult.Value);
        //}

        //[Fact]
        //public void UpdateImage_ReturnsNotFound_WhenOperatorNotFound2()
        //{
        //    // Arrange
        //    var id = 1;
        //    var newImage = new byte[] { 1, 2, 3 };
        //    _operatorServiceMock.Setup(service => service.UpdateImage(id, newImage)).Returns((Crewoperator)null);

        //    // Act
        //    var result = _controller.UpdateImage(id, newImage);

        //    // Assert
        //    var notFoundResult = Assert.IsType<NotFoundResult>(result); // Assert that it returns NotFoundResult
        //    Assert.Equal(404, notFoundResult.StatusCode); // Verify that the status code is 404
        //}

        //[Fact]
        //public void UpdateImage_ShouldReturnNotFound_WhenOperatorNotFound()
        //{
        //    // Arrange
        //    var id = 1;
        //    var newImage = new byte[] { 1, 2, 3 };
        //    _operatorServiceMock.Setup(service => service.UpdateImage(id, newImage)).Returns((Crewoperator)null);

        //    // Act
        //    var result = _controller.UpdateImage(id, newImage);

        //    // Assert
        //    var notFoundResult = Assert.IsType<NotFoundResult>(result); // Assert NotFoundResult instead of null
        //    Assert.Equal(404, notFoundResult.StatusCode); // Verify the status code is 404
        //}






    }






}
