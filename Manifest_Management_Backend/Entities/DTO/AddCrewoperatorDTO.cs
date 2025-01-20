using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
//using Entities.Models;

namespace ManifestPortal1.Entities.DTO
{
    public class AddCrewoperatorDTO
    {
        public string Salutation { get; set; } // e.g., "Mr", "Mrs", "Miss"

        public string FirstName { get; set; }

        public string LastName { get; set; }

        public string ContactNumber { get; set; }

        public DateTime Dob { get; set; }

        public string Gender { get; set; } // e.g., "M", "F", "O"

        public string CrewUsername { get; set; }

        public string Email { get; set; }

        public string Password { get; set; }
    }




}
