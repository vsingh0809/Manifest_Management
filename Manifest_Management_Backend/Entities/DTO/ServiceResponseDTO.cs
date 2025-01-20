using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ManifestPortal1.Entities.DTO
{
    public class ServiceResponseDTO
    {

        public bool Success { get; set; }
        public int StatusCode { get; set; } = 200; // Default to 200 OK
        public string Message { get; set; }
    }
}
