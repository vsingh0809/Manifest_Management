using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
//using Entities.Models;

namespace ManifestPortal1.Entities.DTO
{
    public class UpdateGuestDTO
    {

        public string? FirstName { get; set; } // Maps to firstName


        public string? LastName { get; set; } // Maps to lastName


        public string Gender { get; set; } // Maps to gender (Enum)

        public DateTime? DOB { get; set; } // Maps to dob


        public string? MobileNo { get; set; } // Maps to mobileNo (Unique)


        public string? Citizenship { get; set; } // Maps to citizenship


        public string? BoardingPoint { get; set; } // Maps to boardingPoint


        public string? DestinationPoint { get; set; } // Maps to destinationPoint


        public string? LastUpdatedBy { get; set; } //Maps to lastUpdatedBy

        public string? LastUpdatedDate { get; set; } //Maps to lastUpdatedDate


    }
}