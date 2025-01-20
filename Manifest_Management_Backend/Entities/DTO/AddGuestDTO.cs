using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
//using Entities.Models;

namespace ManifestPortal1.Entities.DTO
{
    public class AddGuestDTO
    {
        public string FirstName { get; set; }

        public string LastName { get; set; }

        public string Salutation { get; set; } // e.g., "Mr", "Mrs", "Miss"

        public string Gender { get; set; } // e.g., "M", "F", "O"

        public string createdBy { get; set; }

        public string MobileNo { get; set; }
        public DateTime DOB { get; set; }
        public string Email { get; set; }
        public string Citizenship { get; set; }
        public string BoardingPoint { get; set; }

        public string DestinationPoint { get; set; }

        public string LastUpdatedBy { get; set; }
        public string LastUpdatedDate { get; set; }
    }

}

