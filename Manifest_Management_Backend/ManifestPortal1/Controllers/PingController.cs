using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace ManifestPortal1.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class PingController : ControllerBase
    {

        /// <summary>
        /// Checks the status of the application.
        /// </summary>
        /// <returns>
        /// Returns a message indicating that the application is running successfully.
        /// </returns>
        /// <response code="200">Returns a message indicating the application is running.</response>
        [HttpGet]
        public IActionResult Ping()
        {
            return Ok(new { message = "Application is running successfully." });
        }
    
}
}
