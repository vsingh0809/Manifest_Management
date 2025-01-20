using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using ManifestPortal1.Business.Class;
using ManifestPortal1.Business.Contracts;
using ManifestPortal1.Entities.DTO;
using ManifestPortal1.Entities.Models;
using System.Globalization;
using Org.BouncyCastle.Crypto;
using ManifestPortal1.Filters;
namespace ManifestPortal1.Controllers
{


    [Route("api/[controller]")]
    [ApiController]
    [ServiceFilter(typeof(LoggingActionFilter))]
    public class GuestsController : ControllerBase
    {
        private readonly IGuestService _guestService;

        public GuestsController(IGuestService guestService)
        {
            _guestService = guestService;
        }


        // GET: api/guests
        /// <summary>
        /// Retrieves a list of all guests.
        /// </summary>
        /// <returns>
        /// Returns a list of all guests.
        /// </returns>
        /// <response code="200">Returns the list of guests.</response>
        /// <response code="400">If the input parameters are invalid or missin</response>

        [HttpGet]

        public async Task<IActionResult> GetAllGuests()
        {
            IEnumerable<Guest> guests = await _guestService.GetAllGuestsAsync();
            return Ok(guests);
        }



        // GET: api/guests/{id}

        /// <summary>
        /// Retrieves the details of a guest by their unique identifier.
        /// </summary>
        /// <param name="id">The unique identifier of the guest.</param>
        /// <returns>
        /// Returns the details of the guest with the specified ID.
        /// </returns>
        /// <response code="200">Returns the guest details if found.</response>
        /// <response code="404">If no guest is found with the specified ID.</response>
        [HttpGet("{id}")]
        public async Task<IActionResult> GetGuest(int id)
        {
            if (id <= 0)
            {
                return BadRequest("Invalid guest ID provided.");
            }

            try
            {
                var guest = await _guestService.GetGuestAsync(id);
                return Ok(guest);
            }
            catch (KeyNotFoundException ex)
            {
                // Return NotFound with the exception message
                return NotFound(ex.Message);
            }
            catch (Exception ex)
            {
                // Log the exception here for investigation
                // e.g., _logger.LogError(ex, "Error while fetching guest with ID {id}");

                // Return a generic error message for unexpected issues
                return StatusCode(500, "An unexpected error occurred. Please try again later.");
            }
        }




        ////POST: api/guests
        /// <summary>
        /// Registers a new guest.
        /// </summary>
        /// <param name="guestDto">The data transfer object containing the guest information to register.</param>
        /// <returns>
        /// Returns the newly registered guest details.
        /// </returns>
        /// <response code="200">Returns the newly created guest details.</response>
        /// <response code="400">If the guest data is invalid or missing.</response>


        //[HttpPost]
        //public async Task<IActionResult> RegisterGuest([FromBody] AddGuestDTO guestDto)
        //{
        //    if (guestDto == null)
        //    {
        //        return BadRequest(new { message = "Invalid guest data." });
        //    }


        //    var newUser = await _guestService.RegisterGuestAsync(guestDto);


        //    if (newUser == null)
        //    {
        //        return BadRequest(new { message = "Guest registration failed." });
        //    }


        //    return CreatedAtAction(nameof(RegisterGuest), newUser);
        //}





        [HttpPost]
        public async Task<IActionResult> RegisterGuest([FromBody] AddGuestDTO guestDto)
        {
            if (guestDto == null)
            {
                return BadRequest(new { message = "Invalid guest data." });
            }

            try
            {
                var newUser = await _guestService.RegisterGuestAsync(guestDto);

                if (newUser == null)
                {
                    return BadRequest(new { message = "Guest registration failed." });
                }

                return CreatedAtAction(nameof(RegisterGuest), new { id = newUser.GuestId }, newUser);
            }
            catch (InvalidOperationException ex)
            {
                return Conflict(new { message = ex.Message });
            }
            catch (Exception ex)
            {
                // Catch any unexpected errors
                return StatusCode(500, new { message = "An error occurred while registering the guest.", details = ex.Message });
            }
        }




        // PUT: api/guests/{id}
        /// <summary>
        /// Updates an existing guest's details.
        /// </summary>
        /// <param name="id">The unique identifier of the guest to update.</param>
        /// <param name="updatedGuest">The data transfer object containing the updated guest information.</param>
        /// <returns>
        /// Returns a success message if the guest was updated successfully.
        /// </returns>
        /// <response code="200">Returns a success message when the guest is updated successfully.</response>
        /// <response code="400">If the updated guest data is invalid or missing.</response>
        /// <response code="404">If no guest is found with the specified ID.</response>

        //[HttpPut("{id}")]
        //public async Task<IActionResult> UpdateGuest(int id, [FromBody] UpdateGuestDTO updatedGuest)
        //{
        //    if (updatedGuest == null)
        //        return BadRequest(new { message = "Invalid guest data." });

        //    await _guestService.UpdateGuestAsync(id, updatedGuest);
        //    return Ok(new { message = "Guest updated successfully." });
        //}

        [HttpPut("{id}")]
        public async Task<IActionResult> UpdateGuest(int id, UpdateGuestDTO guestDto)
        {
            if (guestDto == null || string.IsNullOrEmpty(guestDto.FirstName))
            {
                return BadRequest("Invalid guest data.");
            }
            try
            {
                var guest = await _guestService.GetGuestAsync(id);
                if (guest == null)
                {
                    return NotFound($"Guest with ID {id} not found.");
                }else if (guest.IsDeleted == "Y")
                {
                    var msg = new Dictionary<string, string>
                    {
                        {"message","Guest is deleted cannot update deleted guest." }
                    };
                    return BadRequest(msg);
                    
                }
                

                await _guestService.UpdateGuestAsync(id, guestDto);

                var result = new Dictionary<string, string>
        {
            { "message", "Guest updated successfully." }
        };

                return Ok(result);
            }
            catch (Exception ex)
            {
                return NotFound(ex.Message);
            }
        }

        // SOFT DELETE
        [HttpDelete("SoftDelete/{id}")]
        public async Task<IActionResult> SoftDeleteGuest(int id)
        {
            if (id <= 0)
            {
                return BadRequest(new { message = "Invalid guest ID provided." });
            }

            try
            {
                await _guestService.SoftDeleteGuestAsync(id);
                return Ok(new { message = "Guest deleted successfully." });
            }
            catch (KeyNotFoundException ex)
            {
                // Handle cases where the guest is not found or already deleted
                return NotFound(new { message = ex.Message });
            }
            catch (Exception ex)
            {
                // Handle other exceptions
                return StatusCode(500, new { message = "An error occurred while deleting the guest.", details = ex.Message });
            }
        }

        //Multiple Soft Delete
        [HttpPost("SoftDelete")]
        public async Task<IActionResult> SoftDeleteMultipleGuests([FromBody] List<int> ids)
        {
            if (ids == null || ids.Count == 0)
            {
                var response = new Dictionary<string, string> { { "message", "No guest IDs provided." } };
                return BadRequest(response);
            }

            try
            {
                await _guestService.SoftMultipleDeleteGuestAsync(ids);
                return Ok("Guest deleted successfully.");
            }
            catch (Exception ex)
            {
                var response = new Dictionary<string, string> { { "message", ex.Message } };
                return StatusCode(500, response);
            }
        }







        // DELETE: api/guests/{id}
        /// <summary>
        /// Deletes a guest by their unique identifier.
        /// </summary>
        /// <param name="id">The unique identifier of the guest to delete.</param>
        /// <returns>
        /// Returns a success message if the guest was deleted successfully.
        /// </returns>
        /// <response code="200">Returns a success message when the guest is deleted successfully.</response>
        /// <response code="404">If no guest is found with the specified ID.</response>
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteGuest(int id)
        {
            try
            {
                await _guestService.DeleteGuestAsync(id);
                return Ok("Guest deleted successfully.");
            }
            catch (KeyNotFoundException)
            {
                return NotFound($"Guest with ID {id} not found.");
            }
        }


        // GET: api/guests/search
        /// <summary>
        /// Searches for guests based on embarkation and debarkation dates.
        /// </summary>
        /// <param name="embarkDate">The embarkation date of the guest (optional).</param>
        /// <param name="dbarkDate">The debarkation date of the guest (optional).</param>
        /// <returns>
        /// Returns a list of guests matching the criteria.
        /// </returns>
        /// <response code="200">Returns the list of guests matching the criteria.</response>
        /// <response code="404">If no guests are found matching the criteria.</response>

        [HttpGet("search")]

        public async Task<IActionResult> SearchGuests([FromQuery] DateTime? embarkDate, [FromQuery] DateTime? dbarkDate)
        {
            var results = await _guestService.SearchGuestsAsync(embarkDate, dbarkDate);
            if (results == null || results.Count == 0)
                return NotFound("No guests found matching the search criteria.");

            return Ok(results);
        }



        


        // API to search by embark date
        [HttpGet("search-by-embark-date/{embarkDate}")]
        public async Task<IActionResult> SearchByEmbarkDate(string embarkDate)
        {
            var results = await _guestService.SerachByEmbarkDate(embarkDate);

            if (results == null || !results.Any())
            {
                return NotFound("No guests found matching the embark date.");
            }

            return Ok(results);
        }

        // API to search by debark date
        [HttpGet("search-by-debark-date/{debarkDate}")]
        public async Task<IActionResult> SearchByDebarkDate(string debarkDate)
        {
            var results = await _guestService.SerachByDebarkDate(debarkDate);

            if (results == null || !results.Any())
            {
                return NotFound("No guests found matching the debark date.");
            }

            return Ok(results);
        }




        //// POST: api/guests/delete

        /// <summary>
        /// Deletes multiple guests based on the provided list of IDs.
        /// </summary>
        /// <param name="ids">A list of guest IDs to delete.</param>
        /// <returns>
        /// - `200 OK`: If guests are deleted successfully.
        /// - `400 Bad Request`: If the list of IDs is null or empty.
        /// - `404 Not Found`: If any of the specified guests are not found.
        /// </returns>

        [HttpPost("delete")]
        public async Task<IActionResult> DeleteGuests([FromBody] List<int> ids)
        {
            if (ids == null || ids.Count == 0)
            {
                var response = new Dictionary<string, string> { { "message", "No guest IDs provided." } };
                return BadRequest(response);
            }

            try
            {
                await _guestService.DeleteGuestsAsync(ids);
                return Ok("Guest deleted successfully.");
            }
            catch (Exception ex)
            {
                var response = new Dictionary<string, string> { { "message", ex.Message } };
                return StatusCode(500, response);
            }
        }




        // GET: api/guests/byTitle
        /// <summary>
        /// Retrieves all guests based on the provided Salutation (title).
        /// </summary>
        /// <param name="salutation">The Salutation (title) of the guests. Possible values: Mr, Mrs, Miss.</param>
        /// <returns>
        /// - `200 OK`: If guests with the specified title are found.
        /// - `404 Not Found`: If no guests with the specified title are found.
        /// </returns>
        [HttpGet("byTitle")]
        public async Task<IActionResult> GetGuestsByTitle([FromQuery] string salutation)
        {
            try
            {
                var guests = await _guestService.GetGuestsBySalutationAsync(salutation);
                if (guests == null || !guests.Any())
                {
                    return NotFound(new { message = "No guests found with the specified title" });
                }

                return Ok(guests);
            }
            catch (Exception ex)
            {
                // Log the exception here if logging is available
                return StatusCode(500, new { message = "An error occurred while fetching guests.", details = ex.Message });
            }
        }


        //// POST: api/guests/checkIn
        /// <summary>
        /// Checks in guest based on the provided barcode
        /// </summary>
        /// <param name="barcode">The barcode of the guest to check in.</param>
        /// <returns>
        /// - `200 OK`: If the guest is checked in successfully.
        /// - `400 Bad Request`: If the guest is already checked in.
        /// - `404 Not Found`: If the guest with the specified barcode is not found.
        /// </returns>
        [HttpPost("checkin")]
        public async Task<ActionResult> CheckInGuestAsync(int barcode)
        {
            return await _guestService.CheckInGuestAsync(barcode);
        }

        //// POST: api/guests/checkOut
        /// <summary>
        /// Check Out guest based on the provided barcode
        /// </summary>
        /// <param name="barcode">The barcode of the guest to check out.</param>
        /// <returns>
        /// - `200 OK`: If the guest is checked out successfully.
        /// - `400 Bad Request`: If the guest is not checked in for check out.
        /// - `404 Not Found`: If the guest with the specified barcode is not found.
        /// </returns>
        [HttpPost("checkout")]
        public async Task<ActionResult> CheckOutGuestAsync(int barcode)
        {
            return await _guestService.CheckOutGuestAsync(barcode);
        }

        // GET: api/guests/ping

        [HttpGet("ping")]
        public IActionResult Ping([FromQuery] string? message = null)
        {
            var responseMessage = string.IsNullOrEmpty(message)
                ? "Ping! Service is running."
                : $"Ping! Received your message: {message}";

            return Ok(new { message = responseMessage });
        }


        [HttpPut("update-guest-image/{guestId}")]
        public async Task<IActionResult> UpdateGuestImage(int guestId, [FromBody] UpdateGuestImageRequestDTO request)
        {
            if (string.IsNullOrEmpty(request.ImageBase64))
            {
                return BadRequest("Image data is required.");
            }

            try
            {
                var result = await _guestService.UpdateGuestImageAsync(guestId, request);

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



    }

}



