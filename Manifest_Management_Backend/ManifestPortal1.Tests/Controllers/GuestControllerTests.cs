using System;
using System.Collections.Generic;
using System.Dynamic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using ManifestPortal1.Business.Class;
using ManifestPortal1.Business.Contracts;
using ManifestPortal1.Controllers;
using ManifestPortal1.Entities.DTO;
using ManifestPortal1.Entities.Models;
using Microsoft.AspNetCore.Mvc;
using Moq;


namespace ManifestPortal1.Tests.Controllers
{
    public class GuestControllerTests
    {

        private readonly GuestsController _guestController;
        private readonly Mock<IGuestService> _mockGuestService;

        public GuestControllerTests()
        {
            _mockGuestService = new Mock<IGuestService>();
            _guestController = new GuestsController(_mockGuestService.Object);
        }

        [Fact]
        public async Task GetAllGuests_ReturnsOk_WhenGuestsExist()
        {
            // Arrange
            var guests = new List<Guest>
            {
                new Guest { GuestId = 1, FirstName = "John", LastName = "Doe" },
                new Guest { GuestId = 2, FirstName = "Jane", LastName = "Doe" }
            };
            _mockGuestService.Setup(s => s.GetAllGuestsAsync()).ReturnsAsync(guests);

            // Act
            var result = await _guestController.GetAllGuests();

            // Assert
            var okResult = Assert.IsType<OkObjectResult>(result);
            var returnedGuests = Assert.IsAssignableFrom<IEnumerable<Guest>>(okResult.Value);
            Assert.Equal(2, returnedGuests.Count());
        }


        [Fact]
        public async Task GetGuest_ReturnsOkResult_WithGuestDetails()
        {
            // Arrange
            var guest = new Guest { GuestId = 1, FirstName = "John", LastName = "Doe" };
            _mockGuestService.Setup(service => service.GetGuestAsync(It.IsAny<int>()))
                             .ReturnsAsync(guest);

            // Act
            var result = await _guestController.GetGuest(1);

            // Assert
            var okResult = Assert.IsType<OkObjectResult>(result);
            var returnValue = Assert.IsType<Guest>(okResult.Value);
            Assert.Equal(1, returnValue.GuestId);
            Assert.Equal("John", returnValue.FirstName);
            Assert.Equal("Doe", returnValue.LastName);
        }

        // Test for RegisterGuest
        [Fact]
        public async Task RegisterGuest_ReturnsCreatedAtActionResult_WithNewGuest()
        {
            // Arrange
            var guestDto = new AddGuestDTO
            {
                FirstName = "John",
                LastName = "Doe",
                Email = "john.doe@example.com",
                MobileNo = "1234567890"
            };
            var newGuest = new Guest { GuestId = 1, FirstName = "John", LastName = "Doe" };

            _mockGuestService.Setup(service => service.RegisterGuestAsync(It.IsAny<AddGuestDTO>()))
                             .ReturnsAsync(newGuest);

            // Act
            var result = await _guestController.RegisterGuest(guestDto);

            // Assert
            var createdAtActionResult = Assert.IsType<CreatedAtActionResult>(result);
            var returnValue = Assert.IsType<Guest>(createdAtActionResult.Value);
            Assert.Equal("John", returnValue.FirstName);
            Assert.Equal("Doe", returnValue.LastName);
        }










        [Fact]
        public async Task SearchGuests_ReturnsOk_WhenGuestsMatchCriteria()
        {
            // Arrange
            var embarkDate = new DateTime(2023, 1, 1);
            var debarkDate = new DateTime(2023, 1, 10);
            var guests = new List<Guest>
            {
                new Guest { GuestId = 1, FirstName = "John", LastName = "Doe", MBarkDate = embarkDate, DBarkDate = debarkDate }
            };

            _mockGuestService.Setup(service => service.SearchGuestsAsync(embarkDate, debarkDate))
                .ReturnsAsync(guests);

            // Act
            var result = await _guestController.SearchGuests(embarkDate, debarkDate);

            // Assert
            var okResult = result as OkObjectResult;
            Assert.NotNull(okResult);
            var returnValue = okResult.Value as IEnumerable<Guest>;
            Assert.NotNull(returnValue);
            Assert.Single(returnValue);
        }

        [Fact]
        public async Task SearchGuests_ReturnsNotFound_WhenNoGuestsMatchCriteria()
        {
            // Arrange
            var embarkDate = new DateTime(2023, 1, 1);
            var debarkDate = new DateTime(2023, 1, 10);

            _mockGuestService.Setup(service => service.SearchGuestsAsync(embarkDate, debarkDate))
                .ReturnsAsync(new List<Guest>());

            // Act
            var result = await _guestController.SearchGuests(embarkDate, debarkDate);

            // Assert
            var notFoundResult = result as NotFoundObjectResult;
            Assert.NotNull(notFoundResult);
            Assert.Equal("No guests found matching the search criteria.", notFoundResult.Value);
        }



        [Fact]
        public async Task SearchByEmbarkDate_ReturnsOk_WhenGuestsMatchDate()
        {
            // Arrange
            var embarkDateString = "2024-12-31";
            var embarkDate = DateTime.ParseExact(embarkDateString, "yyyy-MM-dd", CultureInfo.InvariantCulture);

            var guests = new List<Guest>
    {
        new Guest { GuestId = 1, FirstName = "John", LastName = "Doe", MBarkDate = embarkDate }
    };

            _mockGuestService.Setup(service => service.SerachByEmbarkDate(embarkDateString))
                .ReturnsAsync(guests);

            // Act
            var result = await _guestController.SearchByEmbarkDate(embarkDateString);

            // Assert
            var okResult = result as OkObjectResult;
            Assert.NotNull(okResult);
            var returnValue = okResult.Value as IEnumerable<Guest>;
            Assert.NotNull(returnValue);
            Assert.Single(returnValue);
        }

        [Fact]
        public async Task SearchByEmbarkDate_ReturnsNotFound_WhenNoGuestsMatchDate()
        {
            // Arrange
            var embarkDateString = "2023-01-01";

            _mockGuestService.Setup(service => service.SerachByEmbarkDate(embarkDateString))
                .ReturnsAsync(new List<Guest>());

            // Act
            var result = await _guestController.SearchByEmbarkDate(embarkDateString);

            // Assert
            var notFoundResult = result as NotFoundObjectResult;
            Assert.NotNull(notFoundResult);
            Assert.Equal("No guests found matching the embark date.", notFoundResult.Value);
        }

        //[Fact]
        //public async Task SearchByEmbarkDate_ReturnsBadRequest_WhenDateFormatIsInvalid()
        //{
        //    // Arrange
        //    var invalidDateString = "invalid-date";

        //    // Act
        //    var result = await _guestController.SearchByEmbarkDate(invalidDateString);

        //    // Assert
        //    var badRequestResult = result as BadRequestObjectResult;
        //    Assert.NotNull(badRequestResult);
        //    Assert.Equal("Invalid date format provided.", badRequestResult.Value);
        //}






        [Fact]
        public async Task SearchByDebarkDate_ReturnsOk_WhenGuestsMatchDate()
        {
            // Arrange
            var debarkDateString = "2024-12-31";
            var debarkDate = DateTime.ParseExact(debarkDateString, "yyyy-MM-dd", CultureInfo.InvariantCulture);


            var guests = new List<Guest>
            {
                new Guest { GuestId = 1, FirstName = "John", LastName = "Doe", DBarkDate = debarkDate  }
            };


            _mockGuestService.Setup(service => service.SerachByDebarkDate(debarkDateString))
                .ReturnsAsync(guests);

            // Act
            var result = await _guestController.SearchByDebarkDate(debarkDateString);

            // Assert
            var okResult = result as OkObjectResult;
            Assert.NotNull(okResult);
            var returnValue = okResult.Value as IEnumerable<Guest>;
            Assert.NotNull(returnValue);
            Assert.Single(returnValue);
        }

        [Fact]
        public async Task SearchByDebarkDate_ReturnsNotFound_WhenNoGuestsMatchDate()
        {
            // Arrange
            var debarkDate = new DateTime(2023, 1, 10);
            var debarkDate2 = debarkDate.ToString();

            _mockGuestService.Setup(service => service.SearchByDebarkDateAsync(debarkDate))
                .ReturnsAsync(new List<Guest>());

            // Act
            var result = await _guestController.SearchByDebarkDate(debarkDate2);

            // Assert
            var notFoundResult = result as NotFoundObjectResult;
            Assert.NotNull(notFoundResult);
            Assert.Equal("No guests found matching the debark date.", notFoundResult.Value);
        }



        //Update guest's
        [Fact]
        public async Task UpdateGuest_ReturnsOkResult_WhenUpdateIsSuccessful()
        {
            // Arrange
            var guestId = 1000;
            var updateGuestDto = new UpdateGuestDTO
            {
                FirstName = "UpdatedFirstName",
                LastName = "UpdatedLastName",
                Gender = "Male",
                DOB = new DateTime(1990, 1, 1),
                MobileNo = "9876543210",
                Citizenship = "USA",
                BoardingPoint = "New York",
                DestinationPoint = "Los Angeles"
            };

            var existingGuest = new Guest
            {
                GuestId = guestId,
                FirstName = "OldFirstName",
                LastName = "OldLastName",
                Gender = "Female",
                Dob = new DateTime(1990, 1, 1),
                MobileNo = "1234567890",
                Citizenship = "Canada",
                BoardingPoint = "Toronto",
                DestinationPoint = "Vancouver"
            };

            // Mock the service to return the existing guest when GetGuestAsync is called
            _mockGuestService.Setup(service => service.GetGuestAsync(guestId))
                             .ReturnsAsync(existingGuest);

            // Mock the service to simulate a successful update
            _mockGuestService.Setup(service => service.UpdateGuestAsync(guestId, It.IsAny<UpdateGuestDTO>()))
                             .Returns(Task.CompletedTask);

            // Act
            var result = await _guestController.UpdateGuest(guestId, updateGuestDto);

            // Assert
            var okResult = Assert.IsType<OkObjectResult>(result); // Assert that the result is OkObjectResult
            var value = okResult.Value; // Get the value of the OkObjectResult

            // Ensure the value is not null and cast it to Dictionary<string, string>
            Assert.NotNull(value);
            Assert.IsType<Dictionary<string, string>>(value);

            // Retrieve the "message" property and assert its value
            var message = ((Dictionary<string, string>)value)["message"];
            Assert.Equal("Guest updated successfully.", message);
        }









        [Fact]
        public async Task UpdateGuest_ReturnsNotFound_WhenGuestDoesNotExist()
        {
            // Arrange
            var guestId = 1;
            var updateGuestDto = new UpdateGuestDTO
            {
                FirstName = "UpdatedFirstName",
                LastName = "UpdatedLastName",
                Gender = "Male",
                DOB = new DateTime(1990, 1, 1),
                MobileNo = "9876543210",
                Citizenship = "USA",
                BoardingPoint = "New York",
                DestinationPoint = "Los Angeles"
            };

            _mockGuestService.Setup(service => service.UpdateGuestAsync(guestId, It.IsAny<UpdateGuestDTO>()))
                             .Returns(Task.CompletedTask);

            // Act
            var result = await _guestController.UpdateGuest(guestId, updateGuestDto);

            // Assert
            var notFoundResult = Assert.IsType<NotFoundObjectResult>(result);
            Assert.Equal($"Guest with ID {guestId} not found.", notFoundResult.Value);
        }

        [Fact]
        public async Task UpdateGuest_ReturnsBadRequest_WhenDtoIsInvalid()
        {
            // Arrange
            var guestId = 1;
            var updateGuestDto = new UpdateGuestDTO
            {
                FirstName = "",
                LastName = "",
                Gender = "",
                MobileNo = "InvalidNumber",
                DOB = null
            };

            _guestController.ModelState.AddModelError("FirstName", "First name is required.");
            _guestController.ModelState.AddModelError("MobileNo", "Mobile number is invalid.");

            // Act
            var result = await _guestController.UpdateGuest(guestId, updateGuestDto);

            // Assert
            var badRequestResult = Assert.IsType<BadRequestObjectResult>(result);
            Assert.Equal("Invalid guest data.", badRequestResult.Value);
        }

        //Delete Guest's

        [Fact]
        public async Task DeleteGuest_ReturnsOkResult_WhenGuestIsDeletedSuccessfully()
        {
            // Arrange
            var guestId = 1;

            _mockGuestService.Setup(service => service.DeleteGuestAsync(guestId))
                             .Returns(Task.CompletedTask);

            // Act
            var result = await _guestController.DeleteGuest(guestId);

            // Assert
            var okResult = Assert.IsType<OkObjectResult>(result);
            Assert.Equal($"Guest deleted successfully.", okResult.Value);
        }

        [Fact]
        public async Task DeleteGuest_ReturnsNotFound_WhenGuestDoesNotExist()
        {
            // Arrange
            var guestId = 99;

            _mockGuestService.Setup(service => service.DeleteGuestAsync(guestId))
                             .Throws(new KeyNotFoundException($"Guest with ID {guestId} not found."));

            // Act
            var result = await _guestController.DeleteGuest(guestId);

            // Assert
            var notFoundResult = Assert.IsType<NotFoundObjectResult>(result);
            Assert.Equal($"Guest with ID {guestId} not found.", notFoundResult.Value);
        }


        //Delete Multiple Guest's
        [Fact]
        public async Task DeleteGuests_ReturnsBadRequest_WhenNoGuestIdsProvided()
        {
            // Arrange
            var guestIds = new List<int>();

            // Act
            var result = await _guestController.DeleteGuests(guestIds);

            // Assert
            var badRequestResult = Assert.IsType<BadRequestObjectResult>(result);
            var response = badRequestResult.Value as Dictionary<string, string>;
            Assert.NotNull(response);
            Assert.Equal("No guest IDs provided.", response["message"]);
        }


        [Fact]
        public async Task DeleteGuests_ReturnsOk_WhenGuestsAreDeletedSuccessfully()
        {
            // Arrange
            var guestIds = new List<int> { 1, 2, 3 };

            _mockGuestService.Setup(service => service.DeleteGuestsAsync(guestIds))
                             .Returns(Task.CompletedTask);

            // Act
            var result = await _guestController.DeleteGuests(guestIds);

            // Assert
            var okResult = Assert.IsType<OkObjectResult>(result);
            Assert.Equal("Guest deleted successfully.", okResult.Value);
        }
        [Fact]
        public async Task DeleteGuests_ReturnsInternalServerError_WhenExceptionOccurs()
        {
            // Arrange
            var guestIds = new List<int> { 1, 2, 3 };

            _mockGuestService.Setup(service => service.DeleteGuestsAsync(guestIds))
                             .Throws(new Exception("An error occurred while deleting guests."));

            // Act
            var result = await _guestController.DeleteGuests(guestIds);

            // Assert
            var objectResult = Assert.IsType<ObjectResult>(result);
            Assert.Equal(500, objectResult.StatusCode);
            var response = objectResult.Value as Dictionary<string, string>;
            Assert.NotNull(response);
            Assert.Equal("An error occurred while deleting guests.", response["message"]);
        }


        [Fact]
        public async Task DeleteGuests_ReturnsBadRequest_WhenGuestIdsIsNull()
        {
            // Arrange
            List<int> guestIds = null;

            // Act
            var result = await _guestController.DeleteGuests(guestIds);

            // Assert
            var badRequestResult = Assert.IsType<BadRequestObjectResult>(result);
            var response = badRequestResult.Value as Dictionary<string, string>;
            Assert.NotNull(response);
            Assert.Equal("No guest IDs provided.", response["message"]);
        }


        [Fact]
        public async Task SoftDeleteGuest_ValidId_ReturnsOkResult()
        {
            // Arrange
            int validId = 1;
            _mockGuestService.Setup(s => s.SoftDeleteGuestAsync(validId)).Returns(Task.CompletedTask);

            // Act
            var result = await _guestController.SoftDeleteGuest(validId);

            // Assert
            var okResult = Assert.IsType<OkObjectResult>(result);
            Assert.Equal(200, okResult.StatusCode);
            Assert.Contains("Guest deleted successfully", okResult.Value.ToString());
        }

        [Fact]
        public async Task SoftDeleteGuest_InvalidId_ReturnsBadRequest()
        {
            // Arrange
            int invalidId = 0;

            // Act
            var result = await _guestController.SoftDeleteGuest(invalidId);

            // Assert
            var badRequestResult = Assert.IsType<BadRequestObjectResult>(result);
            Assert.Equal(400, badRequestResult.StatusCode);
            Assert.Contains("Invalid guest ID provided", badRequestResult.Value.ToString());
        }

        [Fact]
        public async Task SoftDeleteGuest_GuestNotFound_ReturnsNotFound()
        {
            // Arrange
            int nonExistentId = 999;
            _mockGuestService.Setup(s => s.SoftDeleteGuestAsync(nonExistentId))
                             .ThrowsAsync(new KeyNotFoundException("Guest with ID 999 not found."));

            // Act
            var result = await _guestController.SoftDeleteGuest(nonExistentId);

            // Assert
            var notFoundResult = Assert.IsType<NotFoundObjectResult>(result);
            Assert.Equal(404, notFoundResult.StatusCode);
            Assert.Contains("Guest with ID 999 not found", notFoundResult.Value.ToString());
        }

        [Fact]
        public async Task SoftDeleteGuest_UnhandledException_ReturnsInternalServerError()
        {
            // Arrange
            int guestId = 1;
            _mockGuestService.Setup(s => s.SoftDeleteGuestAsync(guestId))
                             .ThrowsAsync(new Exception("Unexpected error"));

            // Act
            var result = await _guestController.SoftDeleteGuest(guestId);

            // Assert
            var internalServerErrorResult = Assert.IsType<ObjectResult>(result);
            Assert.Equal(500, internalServerErrorResult.StatusCode);
            Assert.Contains("An error occurred while deleting the guest", internalServerErrorResult.Value.ToString());
        }



        [Fact]
        public async Task GetGuestsByTitle_ValidSalutation_ReturnsOkResult()
        {
            // Arrange
            string salutation = "Mr.";
            var mockGuests = new List<Guest>
    {
        new Guest { GuestId = 1, FirstName = "John Doe", Salutation = "Mr" },
        new Guest { GuestId = 2, LastName = "James Smith", Salutation = "Mr" }
    };

            _mockGuestService.Setup(s => s.GetGuestsBySalutationAsync(salutation)).ReturnsAsync(mockGuests);

            // Act
            var result = await _guestController.GetGuestsByTitle(salutation);

            // Assert
            var okResult = Assert.IsType<OkObjectResult>(result);
            Assert.Equal(200, okResult.StatusCode);
            var returnedGuests = Assert.IsType<List<Guest>>(okResult.Value);
            Assert.Equal(2, returnedGuests.Count);
        }

        [Fact]
        public async Task GetGuestsByTitle_ValidSalutationNoResults_ReturnsNotFound()
        {
            // Arrange
            string salutation = "Mr";
            _mockGuestService.Setup(s => s.GetGuestsBySalutationAsync(salutation)).ReturnsAsync(new List<Guest>());

            // Act
            var result = await _guestController.GetGuestsByTitle(salutation);

            // Assert
            var notFoundResult = Assert.IsType<NotFoundObjectResult>(result);
            Assert.Equal(404, notFoundResult.StatusCode);
            Assert.Contains("No guests found with the specified title", notFoundResult.Value.ToString());
        }

        [Fact]
        public async Task GetGuestsByTitle_NullOrEmptySalutation_ReturnsNotFound()
        {
            // Arrange
            string salutation = null;
            _mockGuestService.Setup(s => s.GetGuestsBySalutationAsync(salutation)).ReturnsAsync(new List<Guest>());

            // Act
            var result = await _guestController.GetGuestsByTitle(salutation);

            // Assert
            var notFoundResult = Assert.IsType<NotFoundObjectResult>(result);
            Assert.Equal(404, notFoundResult.StatusCode);
            Assert.Contains("No guests found with the specified title", notFoundResult.Value.ToString());
        }

        [Fact]
        public async Task GetGuestsByTitle_UnhandledException_ReturnsInternalServerError()
        {
            // Arrange
            string salutation = "Mrs.";
            _mockGuestService.Setup(s => s.GetGuestsBySalutationAsync(salutation))
                             .ThrowsAsync(new Exception("Unexpected error"));

            // Act
            var result = await _guestController.GetGuestsByTitle(salutation);

            // Assert
            var internalServerErrorResult = Assert.IsType<ObjectResult>(result);
            Assert.Equal(500, internalServerErrorResult.StatusCode);
            Assert.Contains("An error occurred", internalServerErrorResult.Value.ToString());
        }
        [Fact]
        public async Task CheckInGuestAsync_ValidBarcode_ReturnsOkResult()
        {
            // Arrange
            int barcode = 123;
            _mockGuestService.Setup(s => s.CheckInGuestAsync(barcode))
                .ReturnsAsync(new OkObjectResult(new { message = "Check-in successful." }));

            // Act
            var result = await _guestController.CheckInGuestAsync(barcode);

            // Assert
            var okResult = Assert.IsType<OkObjectResult>(result);
            Assert.Equal(200, okResult.StatusCode);
            Assert.Contains("Check-in successful", okResult.Value.ToString());
        }
        [Fact]
        public async Task CheckInGuestAsync_GuestNotFound_ReturnsNotFoundResult()
        {
            // Arrange
            int barcode = 999;
            _mockGuestService.Setup(s => s.CheckInGuestAsync(barcode))
                .ReturnsAsync(new NotFoundObjectResult(new { message = $"Guest with ID {barcode} not found." }));

            // Act
            var result = await _guestController.CheckInGuestAsync(barcode);

            // Assert
            var notFoundResult = Assert.IsType<NotFoundObjectResult>(result);
            Assert.Equal(404, notFoundResult.StatusCode);
            Assert.Contains($"Guest with ID {barcode} not found", notFoundResult.Value.ToString());
        }
        [Fact]
        public async Task CheckInGuestAsync_GuestDeleted_ReturnsBadRequest()
        {
            // Arrange
            int barcode = 456;
            _mockGuestService.Setup(s => s.CheckInGuestAsync(barcode))
                .ReturnsAsync(new BadRequestObjectResult(new { message = "Guest was deleted" }));

            // Act
            var result = await _guestController.CheckInGuestAsync(barcode);

            // Assert
            var badRequestResult = Assert.IsType<BadRequestObjectResult>(result);
            Assert.Equal(400, badRequestResult.StatusCode);
            Assert.Contains("Guest was deleted", badRequestResult.Value.ToString());
        }

        [Fact]
        public async Task CheckInGuestAsync_AlreadyCheckedIn_ReturnsBadRequest()
        {
            // Arrange
            int barcode = 789;
            _mockGuestService.Setup(s => s.CheckInGuestAsync(barcode))
                .ReturnsAsync(new BadRequestObjectResult(new { message = "Guest with Id 789 and name: John Doe was deleted" }));

            // Act
            var result = await _guestController.CheckInGuestAsync(barcode);

            // Assert
            var badRequestResult = Assert.IsType<BadRequestObjectResult>(result);
            Assert.Equal(400, badRequestResult.StatusCode);
            Assert.Contains("Guest with Id 789 and name: John Doe was deleted", badRequestResult.Value.ToString());
        }

        [Fact]
        public async Task CheckInGuestAsync_UnhandledException_ReturnsInternalServerError()
        {
            // Arrange
            int barcode = 123;
            _mockGuestService.Setup(s => s.CheckInGuestAsync(barcode))
                .ThrowsAsync(new Exception("Unexpected error"));

            // Act
            Func<Task> action = async () => await _guestController.CheckInGuestAsync(barcode);

            // Assert
            var exception = await Assert.ThrowsAsync<Exception>(action);
            Assert.Equal("Unexpected error", exception.Message);
        }


        [Fact]
        public async Task CheckOutGuestAsync_ShouldReturnOk_WhenCheckOutIsSuccessful()
        {
            // Arrange
            var barcode = 123;
            var expectedMessage = "Check-out successful.";

            // Mock the service layer to return a successful response
            _mockGuestService.Setup(service => service.CheckOutGuestAsync(barcode))
                .ReturnsAsync(new OkObjectResult(new { message = expectedMessage }));

            // Act
            var result = await _guestController.CheckOutGuestAsync(barcode);

            // Assert
            var okResult = Assert.IsType<OkObjectResult>(result); // Verify the result type
            Assert.Equal(expectedMessage, ((dynamic)okResult.Value).message); // Check if message is correct
        }

        [Fact]
        public async Task CheckOutGuestAsync_ShouldReturnNotFound_WhenGuestNotFound()
        {
            // Arrange
            var barcode = 123;
            var expectedMessage = "Guest with ID 123 not found.";

            // Mock the service layer to return a NotFound result
            _mockGuestService.Setup(service => service.CheckOutGuestAsync(barcode))
                .ReturnsAsync(new NotFoundObjectResult(new { message = expectedMessage }));

            // Act
            var result = await _guestController.CheckOutGuestAsync(barcode);

            // Assert
            var notFoundResult = Assert.IsType<NotFoundObjectResult>(result); // Verify the result type
            Assert.Equal(expectedMessage, ((dynamic)notFoundResult.Value).message); // Check if message is correct
        }

        [Fact]
        public async Task CheckOutGuestAsync_ShouldReturnBadRequest_WhenGuestIsDeleted()
        {
            // Arrange
            var barcode = 123;
            var guestName = "John Doe";
            var expectedMessage = $"Guest with Id 123 and name: {guestName} was deleted";

            // Mock the service layer to return a BadRequest result
            _mockGuestService.Setup(service => service.CheckOutGuestAsync(barcode))
                .ReturnsAsync(new BadRequestObjectResult(new { message = expectedMessage }));

            // Act
            var result = await _guestController.CheckOutGuestAsync(barcode);

            // Assert
            var badRequestResult = Assert.IsType<BadRequestObjectResult>(result); // Verify the result type
            Assert.Equal(expectedMessage, ((dynamic)badRequestResult.Value).message); // Check if message is correct
        }


        [Fact]
        public async Task CheckOutGuestAsync_ShouldReturnBadRequest_WhenGuestNotCheckedIn()
        {
            // Arrange
            var barcode = 123;
            var expectedMessage = "Guest must be checked in to check out.";

            // Mock the service layer to return a BadRequest result
            _mockGuestService.Setup(service => service.CheckOutGuestAsync(barcode))
                .ReturnsAsync(new BadRequestObjectResult(new { message = expectedMessage }));

            // Act
            var result = await _guestController.CheckOutGuestAsync(barcode);

            // Assert
            var badRequestResult = Assert.IsType<BadRequestObjectResult>(result); // Verify the result type
            Assert.Equal(expectedMessage, ((dynamic)badRequestResult.Value).message); // Check if message is correct
        }






    }
}


